#!/bin/bash

JAVA="java"
APP_HOME="`pwd`"
DEFAULT_JVM_OPTS=
CMD_LINE_ARGS=$@
CLASSPATH=$APP_HOME/lib/*
MAIN_CLASS="com.warxim.petep.Main"
LOG_FILE=petep.log

if [[ $2 == '--nogui' ]];
then
   $JAVA -cp "$CLASSPATH" $MAIN_CLASS $CMD_LINE_ARGS
else
   nohup $JAVA -cp "$CLASSPATH" $MAIN_CLASS $CMD_LINE_ARGS > $LOG_FILE &
fi