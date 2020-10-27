#!/bin/bash
#
# Description:  Control script for OSEE Application Server
###################################################################

echo "Start of OSEE control script"

if [ -z "$OSEE_APP_SERVER_PORT" ]; then
  OSEE_APP_SERVER_PORT=8089 
fi

if [ -z "$OSGI_TELNET_PORT" ]; then
  OSGI_TELNET_PORT=$(($OSEE_APP_SERVER_PORT+1))
fi

if [ -z "$INSTALL_PATH" ]; then
  INSTALL_PATH=$(dirname "$BASH_SOURCE")
fi

if [ -z "$OSEE_APP_SERVER_DATA" ]; then
  OSEE_APP_SERVER_DATA=$INSTALL_PATH/../osee_server_data
fi

if [ -z "$OSEE_SERVER_MAX_MEMORY" ]; then
  OSEE_SERVER_MAX_MEMORY=3G
fi

if [ -z "$LOG_BACK" ]; then
  LOG_BACK="logback/logback-config.xml"
fi

if [ -z "$USE_LOAD_BALANCER" ]; then
  USE_LOAD_BALANCER=false
fi

if [ -z "$OSEE_AUTHENTICATION_PROTOCOL" ]; then
  OSEE_AUTHENTICATION_PROTOCOL="trustAll"
fi

if [ -z "$OSEE_BALANCER_GROUP" ]; then
  OSEE_BALANCER_GROUP="osee"
fi

####################################################################

EQUINOX_LAUNCHER=`ls plugins/org.eclipse.equinox.launcher_*.jar`

# Source function library.
[ -z "$JAVA_HOME" -a -x /etc/profile.d/java.sh ] && . /etc/profile.d/java.sh

if [ -d "$JAVA_HOME" ]; then
  JAVA_EXEC=$JAVA_HOME/bin/java
else
  JAVA_EXEC=`which java`
fi

OS_NAME=`uname -s`
OS_MACHINE=`uname -n`

SERVER_ID="$OS_MACHINE"_"$OSEE_APP_SERVER_PORT"
CONFIGURATION_AREA="$INSTALL_PATH/$SERVER_ID.config"
mkdir -p "$CONFIGURATION_AREA"


LOG="$INSTALL_PATH/logs/osee_app_server_$SERVER_ID.log"
LOCK="$INSTALL_PATH/locks/osee_app_server_$SERVER_ID.lock"

OSEE_APP_SERVER_EXTRA_VMARGS="-Djavax.net.ssl.trustStore=$INSTALL_PATH/etc/keystore/lba.jks -Djavax.net.ssl.trustStorePassword=secret -Dcm.config.uri=$OSEE_SERVER_CONFIG_URI -Djava.security.egd=file:///dev/urandom -Dosee.authentication.protocol=$OSEE_AUTHENTICATION_PROTOCOL -Dosee.application.server.data=$OSEE_APP_SERVER_DATA -Dosee.check.tag.queue.on.startup=false -Dosee.proxy.bypass.enabled=true"

######################################################################
                                        
RETVAL=0

BOOTUP=color
RES_COL=3
MOVE_TO_COL="echo -en \\033[${RES_COL}G"
SETCOLOR_SUCCESS="echo -en \\033[1;32m"
SETCOLOR_FAILURE="echo -en \\033[1;31m"
SETCOLOR_NORMAL="echo -en \\033[0;39m"

echo_success() {
  [ "$BOOTUP" = "color" ] && $MOVE_TO_COL
  echo -n "["
  [ "$BOOTUP" = "color" ] && $SETCOLOR_SUCCESS
  echo -n $"  OK  "
  [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
  echo -n "]"
  echo -ne "\r"
  return 0
}

echo_failure() {
  [ "$BOOTUP" = "color" ] && $MOVE_TO_COL
  echo -n "["
  [ "$BOOTUP" = "color" ] && $SETCOLOR_FAILURE
  echo -n $"FAILED"
  [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
  echo -n "]"
  echo -ne "\r"
  return 1
}

success() {
  #if [ -z "${IN_INITLOG:-}" ]; then
  #   initlog $INITLOG_ARGS -n $0 -s "$3" -e 1
  #fi
  [ "$BOOTUP" != "verbose" -a -z "${LSB:-}" ] && echo_success
  return 0
}

# Log that something failed
failure() {
  rc=$?
  #if [ -z "${IN_INITLOG:-}" ]; then
  #   initlog $INITLOG_ARGS -n $0 -s "$3" -e 2
  #fi
  [ "$BOOTUP" != "verbose" -a -z "${LSB:-}" ] && echo_failure
  [ -x /usr/bin/rhgb-client ] && /usr/bin/rhgb-client --details=yes
  [ -w /var/gdm/.gdmfifo ] && echo "BOOTUP_ERRORS" > /var/gdm/.gdmfifo &
  return $rc
}

print_osee_app_server_home(){
   echo "OSEE App Server Home: [$INSTALL_PATH]"
   echo ""
}

test_app_server_alive() {
 (expect -c "
     log_user 0
     set timeout 10
     spawn telnet $OS_MACHINE $OSGI_TELNET_PORT
     expect_after eof {exit 1}
     expect \"osgi> \" {send \"disconnect\r\"}
     expect \"default=y) \" {send \"y\r\"}
     exit 0
   ")
   isAlive=$?
   if [ $isAlive -eq 0 ]; then
      isAlive="Alive"
   fi
}

pid_of_osee_app_server() {  
  pid=`netstat -tulpn | grep $OSEE_APP_SERVER_PORT | awk '{print $1}'`
}

start() {

    NMAP=$(nmap -p ${OSEE_APP_SERVER_PORT} localhost)
    if [[ $NMAP != *"closed"* ]]; then
           echo "OSEE port ${OSEE_APP_SERVER_PORT} already in use";
           exit 1
    fi
    [ -e "$LOG" ] && cnt=`wc -l "$LOG" | awk '{ print $3 }'` || cnt=1

    echo $"Starting OSEE App Server: "
    
    mkdir -p "$INSTALL_PATH"/logs
    mkdir -p "$INSTALL_PATH"/locks
    
    pid_of_osee_app_server
    if [ -n "$pid" ]; then
           echo Server already started with pid: $pid
           exit 0
    fi
    echo $OSEE_SERVER_CONFIG_URI    
    EXECUTION_CMD="$JAVA_EXEC -Xms40m -Xmx$OSEE_SERVER_MAX_MEMORY -Dorg.osgi.service.http.port=$OSEE_APP_SERVER_PORT -Dlogback.configurationFile=$LOG_BACK $OSEE_APP_SERVER_EXTRA_VMARGS -jar $EQUINOX_LAUNCHER -console $OSGI_TELNET_PORT -consoleLog $OSEE_APP_ARGS -clean>> $LOG 2>&1 &" 
    EXECUTION_CMD=${EXECUTION_CMD//Program Files/\'Program Files\'}
    echo "EXEC: $EXECUTION_CMD"> "$LOG"
   
    `eval` $EXECUTION_CMD

    pid_of_osee_app_server 
    echo pid: $pid
    RETVAL=$?
     if [ -z "$pid" ]; then
         echo Server not started for port $OSEE_APP_SERVER_PORT
       echo "[  FAILED  ]"
       elif [ $RETVAL == 0 ]; then
          echo Server started with port $OSEE_APP_SERVER_PORT and pid $pid
          echo "[  OK  ]"
       else 
          echo Server not started with port $OSEE_APP_SERVER_PORT
          echo "[ FAILED ]"   
    fi

    [ $RETVAL = 0 ] && `echo "" > "$LOCK"`
}

stop() {
    echo -n $"Stopping OSEE App Server: "

    pid_of_osee_app_server
	 echo pid: $pid
    # process still alive, try kill
    if [ -n "$pid" ]; then
 		 kill $(lsof -t -i:OSEE_APP_SERVER_PORT)
       RETVAL=$?
       if [ $RETVAL == 0 ]; then
         # kill successfully sent to process
         sleep 5
         pid_of_osee_app_server
         if [ -n "$pid" ]; then
            # server still alive, kill -9
             kill -9 $recheck
         fi
      fi
    fi

    rm -rf "$CONFIGURATION_AREA"
    rm -f "$LOCK"
    RETVAL=$?
    if [ $RETVAL == 0 ]; then
          echo "[  OK  ]"
    else
           echo "[FAILED]"
    fi
}

status() {
   pid_of_osee_app_server
   
   if [ -n "$pid" ]; then
       
          test_app_server_alive

         if [ "$isAlive" == "Alive" ]; then
              echo "OSEE App Server (pid $pid) is running..."
         else 
              echo "OSGI without OSEE (pid $pid) is running..."
       fi
             
   fi
  
   if [ -z "$pid" ]; then
                echo No pid found for port $OSEE_APP_SERVER_PORT                  
           else
             echo Server is running for port $OSEE_APP_SERVER_PORT
             return 0
   fi
   echo "OSEE App Server is stopped"
   return 3
}

setLoadBalancerPortEnablement () {
  if [ "$USE_LOAD_BALANCER" = true ]; then
    source $INSTALL_PATH/balancer.sh
    if [ $ACTION == "disable" ]; then
       sleep 5
    fi
  fi
}

# See how we were called.
OSEE_APP_SERVER_CMD=$1
case "$OSEE_APP_SERVER_CMD" in
  start)
    print_osee_app_server_home
    start
    if [ $RETVAL == 0 ]; then
      ACTION="enable"
      setLoadBalancerPortEnablement
    else
       echo "OSEE Application Server do not start"
    fi
    ;;
  stop)
    print_osee_app_server_home
      ACTION="disable"
      setLoadBalancerPortEnablement
    stop
    ;;
  status)
    print_osee_app_server_home
    status
    ;;
  restart)
    stop
    start
    ;;
  *)
    echo $"Usage: {start|stop|restart|status}"
    exit 1
esac


exit $RETVAL

