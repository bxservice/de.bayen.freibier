#!/bin/bash
#
# usage:
# deploy.sh <install> <installation>
# deploy.sh firstinstall <installation>
# deploy.sh update <installation>


set -e
set -u

echo deploy.sh
COMMAND=${1}
INSTALLATION=${2}
SSH_USER=`./simpleDB installations read ${INSTALLATION} username`
echo $SSH_USER
SERVER=`./simpleDB installations read ${INSTALLATION} server`
echo $SERVER
SERVERNAME=`./simpleDB server read ${SERVER} servername`
echo $SERVERNAME
DATABASE=`./simpleDB installations read ${INSTALLATION} database`
echo $DATABASE
IDEMPIERESOURCEDIR=`./simpleDB installations read ${INSTALLATION} idempieresource`
echo $IDEMPIERESOURCEDIR
SSHCOMMAND="ssh ${SSH_USER}@${SERVERNAME}"
echo $SSHCOMMAND
CONSOLEPORT=`./simpleDB installations read ${INSTALLATION} consoleport`
POSTGRESPW=`./simpleDB server read ${SERVER} postgrespw`
POSTGRESUSER=`./simpleDB installations read ${INSTALLATION} postgresuser`
POSTGRESUSERPW=`./simpleDB installations read ${INSTALLATION} postgrespw`


#set -v
#PLUGINS=( `ls deploy/plugins/ | perl -pe 's/(^.*\w)_\d.*$/$1/' | uniq | sort ` )
PLUGINS=`./simpleDB installations read ${INSTALLATION} plugins`

inituser () {
	echo 'inituser()'
#	ssh root@${SERVERNAME} adduser --disabled-login --gecos ${SSH_USER} ${SSH_USER}
	ssh root@${SERVERNAME} mkdir -p /home/${SSH_USER}/.ssh/		
	ssh root@${SERVERNAME} chown ${SSH_USER}.${SSH_USER} /home/${SSH_USER}/.ssh/
	ssh root@${SERVERNAME} chmod 0700 /home/${SSH_USER}/.ssh/
	scp ~/.ssh/id_rsa.pub root@${SERVERNAME}:/home/${SSH_USER}/.ssh/authorized_keys
	ssh root@${SERVERNAME} chown ${SSH_USER}.${SSH_USER} /home/${SSH_USER}/.ssh/authorized_keys
	ssh root@${SERVERNAME} chmod 0600 /home/${SSH_USER}/.ssh/authorized_keys
        echo | ${SSHCOMMAND} 'cat >.pgpass' <<END
localhost:5432:*:${POSTGRESUSER}:${POSTGRESUSERPW}
END
	${SSHCOMMAND} chmod 0600 .pgpass
}

serverstop () {
	echo 'serverstop()'
	set +e
	$SSHCOMMAND sudo /etc/init.d/idempiere-${INSTALLATION}.sh stop
	set -e
}

serverstart () {
	echo 'serverstart()'
	$SSHCOMMAND sudo /etc/init.d/idempiere-${INSTALLATION}.sh start
}

# TODO das sollte als erstes auf PLUGINS umgestellt werden
copyallpackages () {
	echo 'copyallpackages()'
	# copy server package and bundles to the cloudserver
	PLUGINS=$@
	#echo copy idempiereServer.gtk.linux.x86_64.zip
	rsync -P ~/buckminster.output/org.adempiere.server_2.0.0-eclipse.feature/idempiereServer.gtk.linux.x86_64.zip ${SSH_USER}@${SERVERNAME}:.
	# alternativ geht auch folgendes auf dem Server:
	# wget http://ci.idempiere.org/job/iDempiere/ws/buckminster.output/org.adempiere.server_2.0.0-eclipse.feature/idempiereServer.gtk.linux.x86_64.zip
	for plugin in ${PLUGINS[@]}
	do
		lastversion=`cd ../deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
		#echo copy ${lastversion}
		rsync -P ../deploy/plugins/${lastversion} ${SSH_USER}@${SERVERNAME}:plugins
	done
}

firstinstallserverpackage () {
	echo 'firstinstallserverpackage()'
	${SSHCOMMAND} mkdir -p backup
	# install new package
	$SSHCOMMAND rm -rf idempiere.gtk.linux.x86_64
	$SSHCOMMAND unzip -q idempiereServer.gtk.linux.x86_64.zip
	echo | ${SSHCOMMAND} 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && ./console-setup.sh' <<END



sigmundbauer





localhost
8080
8443
N
2
localhost
5432
sigmundbauer
${POSTGRESUSER}
${POSTGRESUSERPW}
${POSTGRESPW}
localhost


info@bx-erp.com
Y
END
	$SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && mkdir -p log'
	$SSHCOMMAND 'perl -pi -e "s/12612/'${CONSOLEPORT}'/" idempiere.gtk.linux.x86_64/idempiere-server/idempiere-server.sh'
	echo | $SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/utils && ./RUN_ImportIdempiere.sh'
	# TODO
	#ssh root@${SERVERNAME} cp /home/${SSH_USER}/idempiere.gtk.linux.x86_64/idempiere-server/utils/unix/idempiere_Debian.sh /etc/init.d/idempiere-${INSTALLATION}.sh
	
}

installserverpackage () {
	echo 'installserverpackage()'
	# hold backups for 45 days
	BACKUPDAYS=45
	set +e
	$SSHCOMMAND mv idempiere.gtk.linux.x86_64 backup/idempiere.gtk.linux.x86_64-`date +%Y%m%d%H%M%S`
	set -e
	$SSHCOMMAND "find backup -maxdepth 1 -name idempiere.gtk.linux.x86_64-* -mtime -${BACKUPDAYS} -exec echo {} ';'"
	# install new package
	$SSHCOMMAND unzip -q idempiereServer.gtk.linux.x86_64.zip
	$SSHCOMMAND cp config/idempiere*.properties idempiere.gtk.linux.x86_64/idempiere-server/
	echo -e '\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n' | $SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && ./console-setup.sh'
	$SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && mkdir -p log'
	$SSHCOMMAND 'perl -pi -e "s/12612/'${CONSOLEPORT}'/" idempiere.gtk.linux.x86_64/idempiere-server/idempiere-server.sh'
}

installbundles () {
	echo 'installbundles()'
	# create file with osgi console commands
	PLUGINS=$@
	$SSHCOMMAND 'rm -f .tmpconsole ; touch .tmpconsole'
	# install bundles
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -qv 'fragment$'
		then
			lastversion=`cd ../deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo install file:`pwd`/plugins/'${lastversion}' >>.tmpconsole'
		fi
	done
	# install fragments
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -q 'fragment$'
		then
			lastversion=`cd ../deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo install file:`pwd`/plugins/'${lastversion}' >>.tmpconsole'
		fi
	done
	# start bundles
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -qv 'fragment$'
		then
			lastversion=`cd ../deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo start '${plugin}' >>.tmpconsole'
		fi
	done
	$SSHCOMMAND 'echo uninstall org.adempiere.report.jasper >>.tmpconsole'
	$SSHCOMMAND 'echo uninstall org.adempiere.report.jasper.library >>.tmpconsole'
	$SSHCOMMAND 'echo exit >>.tmpconsole'
	# execute the osgi console
	$SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && ( ( sleep 10 && cat ~/.tmpconsole ) | java -Dosgi.noShutdown=true -jar plugins/org.eclipse.equinox.launcher_1.*.jar -console )'
	# deinstall pre-configured packages from the osgi configuration/config.ini file
	deinstalljasperpackages ${PLUGINS}
}

deinstalljasperpackages () {
	echo 'deinstalljasperpackages()'
	# This must be called after the new jasper package is installed!
	PLUGINS=$@
	$SSHCOMMAND 'rm -f .tmpconsole ; touch .tmpconsole'
	# install bundles
	for plugin in ${PLUGINS[@]}
	do
		if [ "${plugin}" == "de.bayen.freibier.report" ]; then
			$SSHCOMMAND 'perl -pi -e "s/,reference\\\\:file\\\\:org\\.adempiere\\.report\\.jasper(\\.library)?_[^,]+(?=,)/,/g" idempiere.gtk.linux.x86_64/idempiere-server/configuration/config.ini'
		fi
	done
}

dbmigration () {
	echo 'dbmigration()'
	$SSHCOMMAND './backup.sh &'
	ssh -L 15432:localhost:5432 -N ${SSH_USER}@${SERVERNAME} &
	SSH_PID=$!
	echo "Tunnel PID: " $SSH_PID
	sleep 5;  # wait for the connection
	echo ./syncApplied.sh $DATABASE '-h localhost -p 15432' $POSTGRESUSER $IDEMPIERESOURCEDIR/migration
	./syncApplied.sh $DATABASE '-h localhost -p 15432' $POSTGRESUSER $IDEMPIERESOURCEDIR/migration
	echo kill $SSH_PID
	echo "Tunnel closed"
}

updatebundles () {
	echo 'updatebundles()'
	# create file with osgi console commands
	PLUGINS=$@
	$SSHCOMMAND 'rm -f .tmpconsole ; touch .tmpconsole1'
	$SSHCOMMAND 'rm -f .tmpconsole ; touch .tmpconsole2'
	# install bundles
	for plugin in ${PLUGINS[@]}
	do
		$SSHCOMMAND 'echo uninstall '${plugin}' >>.tmpconsole1'
	done
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -qv 'fragment$'
		then
			lastversion=`cd ../deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo install file:`pwd`/plugins/'${lastversion}' >>.tmpconsole2'
		fi
	done
	# install fragments
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -q 'fragment$'
		then
			lastversion=`cd ../deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo install file:`pwd`/plugins/'${lastversion}' >>.tmpconsole2'
		fi
	done
	# start bundles
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -qv 'fragment$'
		then
			lastversion=`cd ../deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo start '${plugin}' >>.tmpconsole2'
		fi
	done
	$SSHCOMMAND 'echo exit >>.tmpconsole1'
	$SSHCOMMAND 'echo exit >>.tmpconsole2'
	# execute the osgi console
	$SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && ( ( sleep 10 && cat ~/.tmpconsole1 ) | java -Dosgi.noShutdown=true -jar plugins/org.eclipse.equinox.launcher_1.*.jar -console )'
	$SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && ( ( sleep 10 && cat ~/.tmpconsole2 ) | java -Dosgi.noShutdown=true -jar plugins/org.eclipse.equinox.launcher_1.*.jar -console )'
}



if [ "$COMMAND" == "firstinstall" ]; then
#	inituser
#	copyallpackages $PLUGINS
#	firstinstallserverpackage
#	installbundles $PLUGINS
	dbmigration
#	serverstart
fi
if [ "$COMMAND" == "install" ]; then
	serverstop
	copyallpackages $PLUGINS
	installserverpackage
	installbundles $PLUGINS
	dbmigration
	serverstart
fi
if [ "$COMMAND" == "update" ]; then
	serverstop
	copyallpackages $PLUGINS
	updatebundles $PLUGINS
	serverstart
fi
