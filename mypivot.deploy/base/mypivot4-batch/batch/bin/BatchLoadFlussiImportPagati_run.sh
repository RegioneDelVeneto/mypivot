#!/bin/sh

perl -p -e 's/\$\{([^}]+)\}/defined $ENV{$1} ? $ENV{$1} : $&/eg; s/\$\{([^}]+)\}//eg' /conf/loadflussiimportpagati-properties_1.0.properties > /conf/loadflussiimportpagati.properties

export ROOT_TALEND="/batch"
export ROOT_PATH="/batch/jobs/BatchLoadFlussiImportPagati/BatchLoadFlussiImportPagati"

ls -latr  /conf
ls -latr $ROOT_PATH/
ls -latr $ROOT_PATH/../../../lib/*
ls -latr $ROOT_PATH/../lib/*

cat -A /conf/log4j.xml
cat -A /conf/loadflussiimportpagati.properties

java -Xms256M -Xmx1024M -cp $ROOT_PATH/*:$ROOT_PATH/../lib/*:/batch/lib/* manage_flussi_import.main_load_flussi_import_pagati_0_1.Main_Load_Flussi_Import_Pagati --context_param directory_talend=$ROOT_TALEND
