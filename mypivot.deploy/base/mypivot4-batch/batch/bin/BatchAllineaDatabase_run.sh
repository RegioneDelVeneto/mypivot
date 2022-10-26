#!/bin/sh

perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/batchallineadatabase-properties_1.0.properties > /conf/batchallineadatabase.properties
perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/batch-data.properties > /conf/batch.properties

export ROOT_TALEND="/batch"
export ROOT_PATH="/batch/jobs/BatchAllineaDatabase/BatchAllineaDatabase"

ls -latr  /conf
ls -latr $ROOT_PATH/../../../lib/*
ls -latr $ROOT_PATH/../lib/*

cat -A /conf/log4j-talend.xml
cat -A /conf/batchallineadatabase.properties
cat -A /conf/batch.properties

java -Xms256M -Xmx1024M -cp $ROOT_PATH/*:$ROOT_PATH/../lib/*:/batch/lib/* allinea_database.main_allinea_database_0_1.Main_Allinea_Database --context_param directory_talend=$ROOT_TALEND
