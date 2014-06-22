#!/bin/bash
#
# usage:
# deploy.sh <install> <user> <server>
# deploy.sh update <user> <server>

COMMAND=${1:-install}
SSH_USER=${2:-bxservice}
SERVER=${3:-bxservice}
DATABASE=$SSH_USER
DBUSER=$SSH_USER
SSHCOMMAND="ssh ${SSH_USER}@${SERVER}"

#set -v
PLUGINS=( `ls deploy/plugins/ | perl -pe 's/(^.*\w)_\d.*$/$1/' | uniq | sort ` )



serverstop () {
	echo 'serverstop()'
	$SSHCOMMAND sudo /etc/init.d/idempiere-${SSH_USER}.sh stop
}

serverstart () {
	echo 'serverstart()'
	$SSHCOMMAND sudo /etc/init.d/idempiere-${SSH_USER}.sh start
}

# TODO das sollte als erstes auf PLUGINS umgestellt werden
copyallpackages () {
	echo 'copyallpackages()'
	# copy server package and bundles to the cloudserver
	PLUGINS=$@
	#echo copy idempiereServer.gtk.linux.x86_64.zip
	rsync -P ~/buckminster.output/org.adempiere.server_2.0.0-eclipse.feature/idempiereServer.gtk.linux.x86_64.zip ${SSH_USER}@${SERVER}:.
	for plugin in ${PLUGINS[@]}
	do
		lastversion=`cd deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
		#echo copy ${lastversion}
		rsync -P deploy/plugins/${lastversion} ${SSH_USER}@${SERVER}:plugins
	done
}

installserverpackage () {
	echo 'installserverpackage()'
	# hold backups for 45 days
	BACKUPDAYS=45
	$SSHCOMMAND mv idempiere.gtk.linux.x86_64 backup/idempiere.gtk.linux.x86_64-`date +%Y%m%d%H%M%S`
	$SSHCOMMAND find backup -maxdepth 1 -name idempiere.gtk.linux.x86_64-* -mtime -${BACKUPDAYS} -exec echo {} ';'
	# install new package
	$SSHCOMMAND unzip -q idempiereServer.gtk.linux.x86_64.zip
	$SSHCOMMAND cp config/idempiere*.properties idempiere.gtk.linux.x86_64/idempiere-server/
	echo -e '\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n' | $SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && ./console-setup.sh'
	$SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && mkdir -p log'
	$SSHCOMMAND 'perl -pi -e "s/12612/12613/" idempiere.gtk.linux.x86_64/idempiere-server/idempiere-server.sh'
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
			lastversion=`cd deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo install file:`pwd`/plugins/'${lastversion}' >>.tmpconsole'
		fi
	done
	# install fragments
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -q 'fragment$'
		then
			lastversion=`cd deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo install file:`pwd`/plugins/'${lastversion}' >>.tmpconsole'
		fi
	done
	# start bundles
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -qv 'fragment$'
		then
			lastversion=`cd deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo start '${plugin}' >>.tmpconsole'
		fi
	done
	$SSHCOMMAND 'echo uninstall org.adempiere.report.jasper >>.tmpconsole'
	$SSHCOMMAND 'echo uninstall org.adempiere.report.jasper.library >>.tmpconsole'
	$SSHCOMMAND 'echo exit >>.tmpconsole'
	# execute the osgi console
	$SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && ( ( sleep 10 && cat ~/.tmpconsole ) | java -Dosgi.noShutdown=true -jar plugins/org.eclipse.equinox.launcher_1.*.jar -console )'
	# deinstall pre-configured packages from the osgi configuration/config.ini file
	deinstalljasperpackages
}

deinstalljasperpackages () {
	echo 'deinstalljasperpackages()'
	# This must be called after the new jasper package is installed!
	$SSHCOMMAND 'perl -pi -e "s/,reference\\\\:file\\\\:org\\.adempiere\\.report\\.jasper(\\.library)?_[^,]+(?=,)/,/g" idempiere.gtk.linux.x86_64/idempiere-server/configuration/config.ini'
}

dbmigration () {
	echo 'dbmigration()'
	$SSHCOMMAND './backup.sh &'
	ssh -L 15432:localhost:5432 -N ${SSH_USER}@${SERVER} &
	SSH_PID=$!
	echo "Tunnel PID: " $SSH_PID
	sleep 10;  # wait for the connection
	( cd .. ; de.bayen.bx.bxs-feature/syncApplied.sh $DATABASE '-h localhost -p 15432' $DBUSER )
	kill $SSH_PID
	echo "Tunnel closed"
}

updatebundles () {
	echo 'updatebundles()'
	# create file with osgi console commands
	PLUGINS=$@
	$SSHCOMMAND 'rm -f .tmpconsole ; touch .tmpconsole'
	# install bundles
	for plugin in ${PLUGINS[@]}
	do
		$SSHCOMMAND 'echo uninstall '${plugin}' >>.tmpconsole'
	done
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -qv 'fragment$'
		then
			lastversion=`cd deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo install file:`pwd`/plugins/'${lastversion}' >>.tmpconsole'
		fi
	done
	# install fragments
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -q 'fragment$'
		then
			lastversion=`cd deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo install file:`pwd`/plugins/'${lastversion}' >>.tmpconsole'
		fi
	done
	# start bundles
	for plugin in ${PLUGINS[@]}
	do
		if echo $plugin | grep -qv 'fragment$'
		then
			lastversion=`cd deploy/plugins/ && ( ls ${plugin}_*.jar -t | head -n 1 )`
			$SSHCOMMAND 'echo start '${plugin}' >>.tmpconsole'
		fi
	done
	$SSHCOMMAND 'echo exit >>.tmpconsole'
	# execute the osgi console
	$SSHCOMMAND 'cd idempiere.gtk.linux.x86_64/idempiere-server/ && ( ( sleep 10 && cat ~/.tmpconsole ) | java -Dosgi.noShutdown=true -jar plugins/org.eclipse.equinox.launcher_1.*.jar -console )'
}


serverstop
copyallpackages $PLUGINS
if [ "$COMMAND" == "install" ]; then
	installserverpackage
	installbundles $PLUGINS
	dbmigration
fi
if [ "$COMMAND" == "update" ]; then
	updatebundles $PLUGINS
fi
serverstart
