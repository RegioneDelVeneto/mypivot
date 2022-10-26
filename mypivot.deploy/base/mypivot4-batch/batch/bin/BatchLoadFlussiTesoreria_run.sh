#!/bin/sh

perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/loadflussitesoreria-properties_1.0.properties > /conf/loadflussitesoreria.properties

export ROOT_TALEND="/batch"
export ROOT_PATH="$ROOT_TALEND/jobs/BatchLoadFlussiTesoreria/BatchLoadFlussiTesoreria"

java -Xms256M -Xmx1024M -cp $ROOT_PATH/*:$ROOT_PATH/../lib/*:/batch/lib/* manage_flussi_tesoreria.main_load_flussi_tesoreria_queue_0_1.Main_Load_Flussi_Tesoreria_Queue --context_param directory_talend=$ROOT_TALEND
