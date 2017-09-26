DATABASE=${1:-adempiere}
BACKUPDIR=${2:-backup}
pg_dump -h localhost -U adempiere --format=plain -O --serializable-deferrable \
	--file ${BACKUPDIR}/idempiere-${DATABASE}-`date +%Y%m%d%H%M`.sql.gz \
	-Z 9 -b --schema adempiere ${DATABASE}
