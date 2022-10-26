#!/bin/sh

perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/batchflussotesoreriaf2k-properties_1.0.properties > /conf/batchflussotesoreriaf2k.properties

export ROOT_TALEND="/batch"
export ROOT_PATH="$ROOT_TALEND/jobs/BatchFlussoTesoreriaF2K/BatchFlussoTesoreriaF2K"

java -Xms256M -Xmx1024M -cp $ROOT_PATH/*:$ROOT_PATH/../lib/*:/batch/lib/* manage_flusso_tesoreria_f2k.main_manage_flusso_tesoreria_f2k_0_1.Main_Manage_Flusso_Tesoreria_F2K --context_param directory_talend=$ROOT_TALEND
