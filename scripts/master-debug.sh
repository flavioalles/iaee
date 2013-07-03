#!/bin/bash

echo "Starting master..."
#echo "-- Output will be redirected to /var/iaee/.master.tty --"
sleep 1
java -jar /opt/iaee/iaee.jar -m & #> /var/iaee/.master.tty &
#exit 0
