#!/bin/bash

date | tr -s ' ' | cut -d ' ' -f 2,3,4
echo
echo "Starting master..."
echo "-- Output will be redirected to $HOME/.iaee/.master.tty --"
sleep 1
java -jar $HOME/.iaee/iaee.jar -m &> $HOME/.iaee/.master.tty &
for i in {1..5}; do
  sleep 300 # 5 minutes (running)
  echo "Killing master..."
  bash $HOME/.iaee/scripts/killiaee 1
  sleep 60 # 1 minute (halted)
  echo "Re-starting master..."
  echo "-- Output will be redirected to $HOME/.iaee/.master.tty --"
  sleep 1
  java -jar $HOME/.iaee/iaee.jar -m &> $HOME/.iaee/.master.tty &
done
bash $HOME/.iaee/scripts/killiaee 1
date | tr -s ' ' | cut -d ' ' -f 2,3,4
exit 0
