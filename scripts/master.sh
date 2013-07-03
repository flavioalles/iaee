#!/bin/bash

echo "Starting master..."
echo "-- Output will be redirected to $HOME/iaee/.master.tty --"
sleep 1
java -jar /opt/iaee/iaee.jar -m &> $HOME/iaee/.master.tty &
exit 0
