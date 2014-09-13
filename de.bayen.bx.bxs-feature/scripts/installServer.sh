#!/bin/bash

# installs a brand new server and prepares it for iDempiere
# (c) 2014 Thomas Bayen

# Um einen neuen Server aufzusetzen, amcht man folgendes:
# 1) erzeugen einer neuen Zeile in der server-Datenbanktabelle (Passwörter ggf. mit pwgen erzeugen)
# 2) Server als Jiffybox aufsetzen, dabei root-Passwort (wie vorher in die Tabelle geschrieben) angeben
# 3) Zertifikate vorbereiten (muss -noch- manuell gemacht werden)
# 4) dann dieses Skript starten

set -e
set -u

SERVER=${1}
SERVERNAME=`./simpleDB server read ${SERVER} servername`
ROOTPW=`./simpleDB server read ${SERVER} rootpw`
POSTGRESPW=`./simpleDB server read ${SERVER} postgrespw`
SSHCOMMAND="ssh root@${SERVERNAME}"
CERTDIR=`./simpleDB server read ${SERVER} certdir`
CERTFILE=`./simpleDB server read ${SERVER} certfile`
KEYFILE=`./simpleDB server read ${SERVER} keyfile`
CACERTFILE=`./simpleDB server read ${SERVER} cacertfile`

sshaccess () {
	# ssh-access and debian base configurations
	echo ${ROOTPW}
	# TODO Passwort übergeben
	echo $SSHCOMMAND -oStrictHostKeyChecking=no uptime
	$SSHCOMMAND -oStrictHostKeyChecking=no uptime
	# TODO Passwort übergeben
	echo sshpass -p '${ROOTPW}' ssh-copy-id root@${SERVERNAME}
	sshpass -p '${ROOTPW}' ssh-copy-id root@${SERVERNAME}
	${SSHCOMMAND} hostname ${SERVER}
	${SSHCOMMAND} "echo '127.0.0.1 ${SERVER}' >>/etc/hosts"
}

installdependencies () {
	# install dependencies packages
	${SSHCOMMAND} aptitude update
	${SSHCOMMAND} aptitude install -y openjdk-6-jdk
	${SSHCOMMAND} aptitude install -y postgresql-9.1 postgresql-contrib-9.1
	${SSHCOMMAND} aptitude install -y unzip rsync
	${SSHCOMMAND} aptitude install -y nginx
}

initpostgres () {
	# PostgreSQL einrichten
	echo 0
	echo ${SSHCOMMAND} su -l postgres -c "psql -c \"ALTER ROLE postgres WITH ENCRYPTED PASSWORD '${POSTGRESPW}';\""
	${SSHCOMMAND} su -l postgres -c "psql -c \"ALTER ROLE postgres WITH ENCRYPTED PASSWORD '${POSTGRESPW}';\""
	echo 1
	echo | ${SSHCOMMAND} 'cat >.pgpass' <<END
localhost:5432:*:postgres:${POSTGRESPW}
END
	${SSHCOMMAND} chmod 0600 .pgpass
	echo | ${SSHCOMMAND} 'cat >installscript.sh' <<END
#!/bin/bash
su -l postgres -c "psql -c \"ALTER ROLE postgres WITH ENCRYPTED PASSWORD '${POSTGRESPW}';\""
END
	${SSHCOMMAND} chmod a+x installscript.sh
	${SSHCOMMAND} ./installscript.sh
}

initnginx () {
	# nginx einrichten
	${SSHCOMMAND} mkdir -p /etc/nginx/SSL
	scp ${CERTDIR}/${CERTFILE} root@${SERVERNAME}:/etc/nginx/SSL/
	scp ${CERTDIR}/${KEYFILE} root@${SERVERNAME}:/etc/nginx/SSL/
	scp ${CERTDIR}/${CACERTFILE} root@${SERVERNAME}:/etc/nginx/SSL/
	echo | ${SSHCOMMAND} "cat >/etc/nginx/sites-available/${SERVERNAME}" <<END
server {
	listen 80;
	server_name ${SERVERNAME};
	rewrite ^(.*) https://${SERVERNAME}.org permanent;
}
server {
	listen 0.0.0.0:443;
	ssl on;
	ssl_certificate /etc/nginx/SSL/${CERTFILE};
	ssl_certificate_key /etc/nginx/SSL/${KEYFILE};
	server_name ${SERVERNAME};
	access_log /var/log/nginx/${SERVER}.log;
location / {
	proxy_pass http://localhost:8080;
	proxy_set_header X-Real-IP \$remote_addr;
	proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
	proxy_set_header Host \$http_host;
	}
}
END
	${SSHCOMMAND} rm -f /etc/nginx/sites-enabled/default
	${SSHCOMMAND} "cd /etc/nginx/sites-enabled; ln -sf ../sites-available/${SERVERNAME} ."
	${SSHCOMMAND} /etc/init.d/nginx restart
}

initfirewall () {
	${SSHCOMMAND} mkdir -p /etc/firewall
	echo | ${SSHCOMMAND} "cat >/etc/firewall/enable.sh" <<END
#!/bin/sh
# /etc/firewall/enable.sh
# A very basic IPtables / Netfilter script
#
# see here: https://wiki.debian.org/DebianFirewall
PATH='/sbin'
# Flush the tables to apply changes
iptables -F
# Default policy to drop 'everything' but our output to internet
iptables -P FORWARD DROP
iptables -P INPUT DROP
iptables -P OUTPUT ACCEPT
# Allow established connections (the responses to our outgoing traffic)
iptables -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT
# Allow local programs that use loopback (Unix sockets)
iptables -A INPUT -s 127.0.0.0/8 -d 127.0.0.0/8 -i lo -j ACCEPT
# ssh
iptables -A INPUT -p tcp --dport 22 -m state --state NEW -j ACCEPT
# http and https
iptables -A INPUT -p tcp --dport 80 -m state --state NEW -j ACCEPT
iptables -A INPUT -p tcp --dport 443 -m state --state NEW -j ACCEPT
# Ping
iptables -A INPUT -p icmp --icmp-type 8 -s 0/0 -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT
iptables -A OUTPUT -p icmp --icmp-type 0 -d 0/0 -m state --state ESTABLISHED,RELATED -j ACCEPT
END
	echo | ${SSHCOMMAND} "cat >/etc/firewall/disable.sh" <<END
#!/bin/sh
# /etc/firewall/disable.sh
# disable firewall - opens all ports!!!
PATH='/sbin'
# Flush the tables to apply changes
iptables -F
iptables -P FORWARD ACCEPT
iptables -P INPUT ACCEPT
iptables -P OUTPUT ACCEPT
END
	${SSHCOMMAND} chmod a+x /etc/firewall/enable.sh
	${SSHCOMMAND} chmod a+x /etc/firewall/disable.sh
	# das hier muss hinter die Zeile "iface etho inet ...". In der Debian Wheezy Jiffybox klappt das so:
	echo | ${SSHCOMMAND} "cat >>/etc/network/interfaces" <<END
 pre-up /bin/sh /etc/firewall/enable.sh
 post-down /bin/sh /etc/firewall/disable.sh
END
	${SSHCOMMAND} /etc/firewall/enable.sh
}

#sshaccess
#installdependencies
#initfirewall
initpostgres
initnginx

# TODO:
# admin-account anlegen (was bisher "tbayen" war)
#... direkter root-Zugang sperren
