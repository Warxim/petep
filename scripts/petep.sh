#!/bin/bash

# Useful params, change if needed
JAVA="java"
APP_HOME=$(cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)
WORKING_DIR=$APP_HOME
DEFAULT_JVM_OPTS=
CMD_LINE_ARGS=$@
CLASSPATH=$APP_HOME/lib/*
MAIN_CLASS="com.warxim.petep.Main"
LOG_FILE=petep.log

# Set working directory (important for petep.json), by default the startup directory is used
cd $WORKING_DIR

# Run PETEP
if [[ $2 == '--nogui' ]];
then
   $JAVA $DEFAULT_JVM_OPTS -cp "$CLASSPATH" $MAIN_CLASS $CMD_LINE_ARGS
else
   nohup $JAVA $DEFAULT_JVM_OPTS -cp "$CLASSPATH" $MAIN_CLASS $CMD_LINE_ARGS > $LOG_FILE &
fi
