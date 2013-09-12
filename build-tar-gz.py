#!/usr/bin/python

import os.path
import sys
import tarfile
from time import sleep

def main(argv):
   if len(argv) == 0:
      print('Creating iaee.tar.gz...')
      sleep(1)
      tar = tarfile.open(os.getcwd() + "/tmp/iaee.tar.gz", "w:gz")
      tar.add(os.getcwd()+ '/bin/scripts', arcname='iaee/bin')
      tar.add(os.getcwd()+ '/conf/config.properties', arcname='iaee/conf/config.properties')
      tar.add(os.getcwd()+ '/jar', arcname='iaee/jar')
      tar.add(os.getcwd()+ '/install.py', arcname='iaee/install.py')
      tar.add(os.getcwd()+ '/README', arcname='iaee/README')
      tar.close() 
      print('Done.')
      sleep(1)
   else:
      print("Wrong usage. No arguments are expected.")

if __name__ == "__main__":
   main(sys.argv[1:])
