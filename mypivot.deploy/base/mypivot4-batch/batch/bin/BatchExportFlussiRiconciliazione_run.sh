#!/bin/sh

perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/export-riconciliazione-properties_1.0.properties > /conf/export-riconciliazione.properties

export ROOT_PATH="/batch/jobs/BatchExportFlussiRiconciliazione/BatchExportFlussiRiconciliazione"

java -Xms256M -Xmx1024M -cp $ROOT_PATH/*:$ROOT_PATH/../lib/*:/batch/lib/* manage_flussi_riconciliazione.main_export_flussi_riconciliazione_0_1.Main_Export_Flussi_Riconciliazione --context_param directory_talend=/E45

