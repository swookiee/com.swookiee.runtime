#!/bin/bash
#
# init script for the swookiee runtime
#

## Read configuration from /etc/default/swookie
CONFIG_FILE=/etc/default/swookiee
test -f $CONFIG_FILE && . $CONFIG_FILE
test ! -f $CONFIG_FILE && echo "Config not found - starting with defaults."

##
RUNTIME_LOCATION="`dirname \"$0\"`"

## TODO
## enable debug args configurable via export 
COMMAND="java -Dlogback.configurationFile=$RUNTIME_LOCATION/logback.xml\
 -Dosgi.compatibility.bootdelegation=true\
 -Xdebug $GC $MEMORY\
 -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n\
 -jar $RUNTIME_LOCATION/target/runtime/plugins/org.eclipse.osgi_3.9.1.v20140110-1610.jar\
 -clean\
 -configuration $RUNTIME_LOCATION/target/runtime/configuration"

# Check the swookiee runtime status
check_status() {
  local s=`ps ax | grep -m1 '[o]sgi' | awk '{print $1}'`
  echo "$s"
}

start() {

  pid=`check_status`
  
  if [ $pid ] ; then
    echo "swookiee runtime is already started"
    exit 1
  fi

  echo -n "Starting swookiee: "

  nohup $COMMAND > /dev/null 2>&1 &
  echo "OK"
}

stop() {

  pid=`check_status`
  
  if ! [ $pid ] ; then
    echo "swookiee is already stopped"
    exit 1
  fi

  # Kills the swookiee osgi process
  echo "Stopping swookiee"
  kill -9 $pid &
  echo "OK"
}

status() {

  pid=`check_status`
  # If a pid was returned the application is running
  if [ $pid ] ; then
    echo "swookiee is started"
  else
    echo "swookiee is stopped"
  fi

}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart)
    stop
    start
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
esac

exit 0
