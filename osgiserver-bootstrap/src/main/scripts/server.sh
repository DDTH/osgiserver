#!/usr/bin/env bash

# ==================================================
# OSGiServer start/stop script for *NIX
# ==================================================

# Uncomment only one of the two lines below
#ENV_NAME=production
ENV_NAME=development

# from http://stackoverflow.com/questions/242538/unix-shell-script-find-out-which-directory-the-script-file-resides
pushd $(dirname "${0}") > /dev/null
_basedir=$(pwd -L)
# Use "pwd -P" for the path without links. man bash for more info.
popd > /dev/null

# Or you can hardcode the directory as you wish
OSGiSERVER_HOME=$_basedir/..
OSGiSERVER_PID="$OSGiSERVER_HOME/osgiserver.pid"

JAVA_MEM_MB=$2
if [ "$JAVA_MEM_MB" = "" ]
then
    JAVA_MEM_MB=64
fi

_appName_=OSGiSERVER

JAVA=$(which java)
JAVA_OPTS="-server -Xms${JAVA_MEM_MB}m -Xmx${JAVA_MEM_MB}m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -XX:+UseParNewGC -XX:+UseConcMarkSweepGC"
JAVA_OPTS+=("-XX:PrintFLSStatistics=1 -XX:PrintCMSStatistics=1 -XX:+PrintTenuringDistribution -XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:gc -Xloggc:$OSGiSERVER_HOME/logs/garbage.log")
JAVA_OPTS+=("-Dspring.profiles.active=$ENV_NAME")
JAVA_OPTS+=("-Dlog4j.configuration=log4j-$ENV_NAME.xml")
JAVA_OPTS+=("-Dosgiserver.home=$OSGiSERVER_HOME")
JAVA_OPTS+=("-Dosgiserver.osgi.properties=$OSGiSERVER_HOME/bin/osgi-felix.properties")
JAVA_OPTS+=("-classpath $OSGiSERVER_HOME/lib:$OSGiSERVER_HOME/lib/*")
JAVA_OPTS+=("com.github.ddth.osgiserver.bootstrap.StandaloneBootstrap")

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
    echo "Usage: ${0##*/} <{start|stop|jpda}> [JVM mem limit in mb]"
    echo "- start: start the server normally"
    echo "- jpda : start the server with remote debugging on port $JPDA_PORT"
    echo "- stop : stop the server"
    echo
    echo "Example: start server with remote debugging and 64mb memory limit"
    echo "  ${0##*/} jpda 64"
    echo
    exit 1
}

ACTION=$1

case "$ACTION" in
    jpda)
        echo -n "Starting $_appName_ Server in debug mode: "
        
        if [ -f "$OSGiSERVER_PID" ]
        then
            if running $OSGiSERVER_PID
            then
                echo "Already Running!"
                exit 1
            else
                # dead pid file - remove
                rm -f "$OSGiSERVER_PID"
            fi            
        fi
        
        "${RUN_CMD_JPDA[@]}" &
        disown $!
        echo $! > "$OSGiSERVER_PID"
            
        echo "STARTED $_appName_ Server `date`" 

        ;;
    
    start)
        echo -n "Starting $_appName_ Server: "

        if [ -f "$OSGiSERVER_PID" ]
        then
            if running $OSGiSERVER_PID
            then
                echo "Already Running!"
                exit 1
            else
                # dead pid file - remove
                rm -f "$OSGiSERVER_PID"
            fi            
        fi
        
        "${RUN_CMD[@]}" &
        disown $!
        echo $! > "$OSGiSERVER_PID"
            
        echo "STARTED $_appName_ Server `date`" 
        
        ;;

    stop)
        echo -n "Stopping $_appName_: "

        PID=$(cat "$OSGiSERVER_PID" 2>/dev/null)
        kill "$PID" 2>/dev/null

        TIMEOUT=30
        while running $OSGiSERVER_PID; do
            if (( TIMEOUT-- == 0 )); then
                kill -KILL "$PID" 2>/dev/null
            fi
            sleep 1
        done

        rm -f "$OSGiSERVER_PID"
        echo OK
        
        ;;

    *)
        usage
        
        ;;
esac

exit 0
