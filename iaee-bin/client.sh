#!/bin/bash

echo "Starting client..."
echo "-- Output will be redirected to $HOME/iaee/.client.tty --"
sleep 1
java -jar /opt/iaee/iaee.jar -c &> $HOME/iaee/.client.tty &
exit 0
