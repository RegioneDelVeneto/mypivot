#!/bin/sh

perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/flussiexportws-properties_1.0.properties > /conf/flussiexportws.properties

cp -r /batch/conf/BatchFlussiExportWS/log4j* /conf/

export ROOT_TALEND="/batch"
export ROOT_PATH="/batch/jobs/BatchFlussiExportWS/BatchFlussiExportWS"

ls -latr  /conf
ls -latr $ROOT_PATH/../../../lib/*
ls -latr $ROOT_PATH/../lib/*

cat -A /conf/log4j.xml
cat -A /conf/flussiexportws.properties

java -Xms256M -Xmx1024M  -Djavax.net.ssl.trustStore=\E45\keystore\truststore.jks -Djavax.net.ssl.trustStoreType=jks -Djavax.net.ssl.trustStorePassword=password -cp $ROOT_PATH/*:$ROOT_PATH/../lib/*:/batch/lib/* manage_flussi_export.main_ws_flussi_export_0_1.Main_WS_Flussi_Export --context_param directory_talend=$ROOT_TALEND
