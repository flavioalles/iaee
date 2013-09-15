#!/bin/bash

date | tr -s ' ' | cut -d ' ' -f 2,3,4
for i in {1..5}; do
   iaee-client quiet
   sleep 300 # 5 minutes (running)
   iaee-kill client 
   sleep 60 # 1 minute (halted)
done
date | tr -s ' ' | cut -d ' ' -f 2,3,4
exit 0
