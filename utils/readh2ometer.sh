#!/bin/bash

RTLTCP=/usr/local/bin/rtl_tcp
RTLTCP_STARTUP_WAIT=10
RTLAMR=/home/user/gocode/bin/rtlamr
LOGFILE=/var/www/html/h2o.log
METERID=11111111

# make sure rtl_tcp isn't running.
pkill rtl_tcp

# start rtl_tcp - ignoring output.
$RTLTCP >/dev/null 2>&1 &

# give rtl_tcp a little while to start.
sleep $RTLTCP_STARTUP_WAIT

# read meter, append to log file
$RTLAMR -quiet=true -filterid=$METERID -single=true -unique=true -format=json >> $LOGFILE

# kill rtl_tcp
kill -9 %1
