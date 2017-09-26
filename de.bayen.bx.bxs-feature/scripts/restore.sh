#!/bin/bash
BACKUPFILE=${1}
DATABASE=${2:-idempiere}
DBUSER=${3:-adempiere}
sudo /etc/init.d/idempiere-${DATABASE}.sh stop
./backup.sh
psql -h localhost -U $DBUSER $DATABASE -c "DROP DATABASE IF EXISTS ${DATABASE}_killme;"
psql -h localhost -U $DBUSER postgres -c "ALTER DATABASE ${DATABASE} RENAME TO ${DATABASE}_killme;"
psql -h localhost -U $DBUSER postgres -c "CREATE DATABASE ${DATABASE} WITH ENCODING='UTF-8' OWNER=${DBUSER};"
zcat ${BACKUPFILE} | psql -h localhost -U $DBUSER $DATABASE -f -
sudo /etc/init.d/idempiere-${DATABASE}.sh start
