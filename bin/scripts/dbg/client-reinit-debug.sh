#!/bin/bash

echo "Starting client..."
echo "-- Output will be redirected to $HOME/.iaee/.client.tty --"
sleep 1
java -jar $HOME/.iaee/iaee.jar -c &> $HOME/.iaee/.client.tty &
for i in {1..5}; do
  sleep 270 # 4.5 minutes (running)
  echo "Killing client..."
  bash $HOME/.iaee/scripts/killiaee 1
  sleep 90 # 1.5 minutes (halted)
  echo "Re-starting client..."
  echo "-- Output will be redirected to $HOME/.iaee/.client.tty --"
  sleep 1
  java -jar $HOME/.iaee/iaee.jar -c &> $HOME/.iaee/.client.tty &
done
bash $HOME/.iaee/scripts/killiaee 1
exit 0
