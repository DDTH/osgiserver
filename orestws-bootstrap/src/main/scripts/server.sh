#!/usr/bin/env bash

# ==================================================
# ORESTWS start/stop script for *NIX
# ==================================================

# from http://stackoverflow.com/questions/242538/unix-shell-script-find-out-which-directory-the-script-file-resides
pushd $(dirname "${0}") > /dev/null
_basedir=$(pwd -L)
# Use "pwd -P" for the path without links. man bash for more info.
popd > /dev/null

# Or you can hardcode the directory as you wish
ORESTWS_HOME=$_basedir/..
ORESTWS_PID="$ORESTWS_HOME/orestws.pid"

JAVA_MEM_MB=$2
if [ "$JAVA_MEM_MB" = "" ]
then
    JAVA_MEM_MB=64
fi

_appName_=ORESTWS

JAVA=$(which java)
JAVA_OPTS="-server -Xms${JAVA_MEM_MB}m -Xmx${JAVA_MEM_MB}m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -XX:+UseParNewGC -XX:+UseConcMarkSweepGC"
JAVA_OPTS+=("-XX:PrintFLSStatistics=1 -XX:PrintCMSStatistics=1 -XX:+PrintTenuringDistribution -XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:gc -Xloggc:$ORESTWS_HOME/logs/garbage.log")
JAVA_OPTS+=("-Dorestws.home=$ORESTWS_HOME")
JAVA_OPTS+=("-Dorestws.osgi.properties=$ORESTWS_HOME/bin/osgi-felix.properties")
JAVA_OPTS+=("-classpath $ORESTWS_HOME/lib:$ORESTWS_HOME/lib/*")
JAVA_OPTS+=("com.github.ddth.orestws.bootstrap.StandaloneBootstrap")

RUN_CMD=("$JAVA" ${JAVA_OPTS[@]})

JPDA_PORT=8888
RUN_CMD_JPDA=("$JAVA" "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$JPDA_PORT" ${JAVA_OPTS[@]})

running()
{
    local PID=$(cat "$1" 2>/dev/null) || return 1
    kill -0 "$PID" 2>/dev/null
}

usage()
{
    echo "Usage: ${0##*/} <{start|stop}> [JVM mem limit in mb]"
    exit 1
}

ACTION=$1

case "$ACTION" in
    jpda)
        echo -n "Starting $_appName_ Server in debug mode: "
        
        if [ -f "$ORESTWS_PID" ]
        then
            if running $ORESTWS_PID
            then
                echo "Already Running!"
                exit 1
            else
                # dead pid file - remove
                rm -f "$ORESTWS_PID"
            fi            
        fi
        
        "${RUN_CMD_JPDA[@]}" &
        disown $!
        echo $! > "$ORESTWS_PID"
            
        echo "STARTED $_appName_ Server `date`" 

        ;;
    
    start)
        echo -n "Starting $_appName_ Server: "

        if [ -f "$ORESTWS_PID" ]
        then
            if running $ORESTWS_PID
            then
                echo "Already Running!"
                exit 1
            else
                # dead pid file - remove
                rm -f "$ORESTWS_PID"
            fi            
        fi
        
        "${RUN_CMD[@]}" &
        disown $!
        echo $! > "$ORESTWS_PID"
            
        echo "STARTED $_appName_ Server `date`" 
        
        ;;

    stop)
        echo -n "Stopping $_appName_: "

        PID=$(cat "$ORESTWS_PID" 2>/dev/null)
        kill "$PID" 2>/dev/null

        TIMEOUT=30
        while running $ORESTWS_PID; do
            if (( TIMEOUT-- == 0 )); then
                kill -KILL "$PID" 2>/dev/null
            fi
            sleep 1
        done

        rm -f "$ORESTWS_PID"
        echo OK
        
        ;;

    *)
        usage
        
        ;;
esac

exit 0
