#!/usr/bin/python

import os
import os.path
import shutil
import sys
from time import sleep

# iaee installation script

# jar file, configuration files and scripts are placed on a hidden folder that is created on the users home directory (/$HOME/.iaee)
# initialization scripts (iaee-master & iaee-client) are placed on $HOME/bin

def fullinstall(CWD, IAEE_HOME):
   print("Full install.")
   sleep(1)
   # create iaee hidden folder in users HOME
   if not os.path.isdir(IAEE_HOME):
      os.mkdir(IAEE_HOME)
      os.mkdir(IAEE_HOME + '/conf')
      os.mkdir(IAEE_HOME + '/db')
      os.mkdir(IAEE_HOME + '/jar')
      os.mkdir(IAEE_HOME + '/scripts')
      os.mkdir(IAEE_HOME + '/.client')
      os.mkdir(IAEE_HOME + '/.master')
   # /conf: copy 'config.properties'
   #shutil.copy(CWD) 
   # /db: move old db, if any | remove SQLite journal files | create .db.ctrl
   # /jar: copy 'iaee.jar'
   # /scripts: copy scripts to iaee's directory
   # /.client & /.master: creation of hidden files in the hidden directory (*.pid, *.tty) 
   # Create $HOME/bin if it does not exist
   if not os.path.isdir(os.path.expanduser('~') + '/bin'):
      os.mkdir(os.path.expanduser('~') + '/bin')
   # copy iaee initialization scripts and management scripts to $HOME/bin
   # Done
   print("Done.")
   sleep(1)

def clientinstall(CWD, IAEE_HOME):
   print("Client install.")
   sleep(1)
   print("Done.")
   sleep(1)

def helpmsg():
   print("install.py: installs iaee on the users home directory.")
   print
   print("Usage:")
   print("     <python install.py full> installs all files necessary to execution (master & client).")
   print("     <python install.py client> installs only necessary files for client use (to be used in a NFS environment).")
   print("     <python install.py help> prints this help message.")
   print

def gethelpmsg():
   print("Wrong usage.")
   print("Type <python install.py help> for usage instructions.")
   
def main(argv):
   CWD = os.getcwd()
   IAEE_HOME = os.path.expanduser('~') + '/.iaee' 
   if len(argv) == 1:
      if argv[0] == 'full':
         fullinstall(CWD, IAEE_HOME)
      elif argv[0] == 'client':
         clientinstall(CWD, IAEE_HOME)
      elif argv[0] == 'help':
         helpmsg()
      else:
         gethelpmsg()
   else:
      gethelpmsg()


if __name__ == "__main__":
   main(sys.argv[1:])
