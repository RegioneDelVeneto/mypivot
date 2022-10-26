#!/bin/sh

perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/loadflussiexport-properties_1.0.properties > /conf/loadflussiexport.properties

cp -r /batch/conf/BatchLoadFlussiExport/log4j* /conf/

export ROOT_TALEND="/batch"
export ROOT_PATH="/batch/jobs/BatchLoadFlussiExport/BatchLoadFlussiExport"

ls -latr  /conf
ls -latr $ROOT_PATH/../../../lib/*
ls -latr $ROOT_PATH/../lib/*

cat -A /conf/log4j.xml
cat -A /conf/loadflussiexport.properties

java -Xms256M -Xmx1024M -cp $ROOT_PATH/*:$ROOT_PATH/../lib/*:/batch/lib/* manage_flussi_export.main_load_flussi_export_0_1.Main_Load_Flussi_Export --context_param directory_talend=$ROOT_TALEND
