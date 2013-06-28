#!/bin/bash

echo "Starting client..."
echo "-- Output will be redirected to /var/iaee/.client.tty --"
sleep 1
java -jar /opt/iaee/iaee.jar -c & #> /var/iaee/.client.tty &
#exit 0
