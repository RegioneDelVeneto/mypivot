#!/bin/sh

perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/flussirendicontazionews-properties_1.0.properties > /conf/flussirendicontazionews.properties


export ROOT_TALEND="/batch"
export ROOT_PATH="/batch/jobs/BatchFlussiRendicontazioneWS/BatchFlussiRendicontazioneWS"

ls -latr  /conf
ls -latr $ROOT_PATH/../../../lib/*
ls -latr $ROOT_PATH/../lib/*

cat -A /conf/flussirendicontazionews-properties_1.0.properties
cat -A /conf/log4j.xml
cat -A /conf/flussirendicontazionews.properties

java -Xms256M -Xmx1024M -cp $ROOT_PATH/*:$ROOT_PATH/../lib/*:/batch/lib/* manage_flussi_rendicontazione.main_ws_flussi_rendicontazione_0_1.Main_WS_Flussi_Rendicontazione --context_param directory_talend=$ROOT_TALEND
