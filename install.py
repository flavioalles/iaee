#!/usr/bin/python

import os
import os.path
import stat
import shutil
import socket
import sys
from time import sleep

# iaee installation script

# jar file, configuration files and scripts are placed on a hidden folder that is created on the users home directory (/$HOME/.iaee)
# initialization scripts (iaee-master & iaee-client) are placed on $HOME/bin

def createfile(filename):
   fl = open(filename, 'w')
   fl.close()
   return True

def updatecontrolfile(IAEE_HOME):
   # get value from .ctrl.db
   fl = open(IAEE_HOME + '/db/.ctrl.db', 'r')
   n = str(fl.readline())
   if not n:
      n = int(0)
   else:
      n = int(n)
   fl.close()
   # update control file value to n+1
   fl = open(IAEE_HOME + '/db/.ctrl.db', 'w')
   fl.write(str(n+1) + '\n')
   fl.close()
   return n

def movedb(IAEE_HOME):
   # move iaee.db to old/iaee-n.db
   os.rename(IAEE_HOME + '/db/iaee.db', IAEE_HOME + '/db/old/iaee-' + str(updatecontrolfile(IAEE_HOME)) + '.db')
   return True

def iaeehome(IAEE_HOME):
   if not os.path.isdir(IAEE_HOME):
      os.mkdir(IAEE_HOME)
   return True

def conf(CWD, IAEE_HOME):
   if not os.path.isdir(IAEE_HOME + '/conf'):
      os.mkdir(IAEE_HOME + '/conf')
   # /conf: copy 'config.properties'
   conf = open(IAEE_HOME + '/conf/config.properties', 'w')
   aux = open(CWD + '/conf/config.properties', 'r')
   for line in aux:
      if line.split('=')[0] == 'SERVER_ADDRESS':
         addr = raw_input('iaee-master address [press ENTER for default(' + line.split('=')[1].rstrip() + ')]: ')
         if addr:
            conf.write('SERVER_ADDRESS=' + addr + '\n')
         else:
            conf.write(line.rstrip() + '\n')
      else:
         conf.write(line.rstrip() + '\n')
   conf.close()
   aux.close()
   return True

def db(IAEE_HOME):
   # /db: move old db, if any | remove SQLite journal files | create .db.ctrl
   if not os.path.isdir(IAEE_HOME + '/db'):
      os.mkdir(IAEE_HOME + '/db')
      os.mkdir(IAEE_HOME + '/db/old')
      createfile(IAEE_HOME + '/db/.ctrl.db') 
   else:
      if os.path.isfile(IAEE_HOME + '/db/iaee.db'):
         # move old db 
         movedb(IAEE_HOME) 
      if os.path.isfile(IAEE_HOME + '/db/iaee.db-journal'):
         # remove SQLite journal files
         os.remove(IAEE_HOME + '/db/iaee.db-journal')
   return True

def jar(CWD, IAEE_HOME):
   if not os.path.isdir(IAEE_HOME + '/jar'):
      os.mkdir(IAEE_HOME + '/jar')
   # /jar: copy 'iaee.jar'
   shutil.copy(CWD + '/jar/iaee.jar', IAEE_HOME + '/jar')
   return True

def scripts(CWD, IAEE_HOME):
   if not os.path.isdir(IAEE_HOME + '/scripts'):
      os.mkdir(IAEE_HOME + '/scripts')
   # /scripts: copy scripts to iaee's directory
   for dirpath, dirname, filename in os.walk(CWD + '/bin'):
      for fl in filename:
         shutil.copy(os.path.join(dirpath, fl), IAEE_HOME + '/scripts')  
   return True

def hiddenclientdir(IAEE_HOME):
   if not os.path.isdir(IAEE_HOME + '/.client'):
      os.mkdir(IAEE_HOME + '/.client')
   # /.client: creation of hidden files in the hidden directory (*.pid, *.tty) 
   for fileext in ['.pid', '-stdout.tty', '-stderr.tty']:
      createfile(IAEE_HOME + '/.client/client-' + str(socket.gethostname()) + fileext)
   return True

def hiddenmasterdir(IAEE_HOME):
   if not os.path.isdir(IAEE_HOME + '/.master'):
      os.mkdir(IAEE_HOME + '/.master')
   # /.master: creation of hidden files in the hidden directory (*.pid, *.tty) 
   for fileext in ['.pid', '-stdout.tty', '-stderr.tty']:
      createfile(IAEE_HOME + '/.master/master-' + str(socket.gethostname()) + fileext)
   return True

def homebin(CWD):
   # Create $HOME/bin if it does not exist
   if not os.path.isdir(os.path.expanduser('~') + '/bin'):
      os.mkdir(os.path.expanduser('~') + '/bin')
   # copy iaee initialization scripts and management scripts to $HOME/bin
   for dirpath, dirname, filename in os.walk(CWD + '/bin'):
      for fl in filename:
         shutil.copy(os.path.join(dirpath, fl), os.path.expanduser('~') + '/bin')
   for dirpath, dirname, filename in os.walk(os.path.expanduser('~') + '/bin'):
      for fl in filename:
         if fl.startswith('iaee-') or fl.startswith('dbg-'):
            os.chmod(os.path.join(dirpath, fl), stat.S_IRWXU | stat.S_IRGRP | stat.S_IROTH) 
   return True

def fullinstall(CWD, IAEE_HOME):
   print("Full install.")
   sleep(1)
   # create iaee hidden folder in users HOME
   iaeehome(IAEE_HOME) 
   conf(CWD, IAEE_HOME)
   db(IAEE_HOME)
   jar(CWD, IAEE_HOME)
   scripts(CWD, IAEE_HOME)
   hiddenclientdir(IAEE_HOME)            
   hiddenmasterdir(IAEE_HOME)
   homebin(CWD)
   # Done
   print("Done.")
   sleep(1)
   return True

def clientinstall(CWD, IAEE_HOME):
   print("Client install.")
   sleep(1)
   hiddenclientdir(IAEE_HOME)            
   print("Done.")
   sleep(1)
   return True

def helpmsg():
   print("install.py: installs iaee on the users home directory.")
   print
   print("Usage:")
   print("     <python install.py full> installs all files necessary to execution (master & client).")
   print("     <python install.py client> installs only necessary files for client use (to be used in a NFS environment).")
   print("     <python install.py help> prints this help message.")
   return True

def gethelpmsg():
   print("Wrong usage.")
   print("Type <python install.py help> for usage instructions.")
   return True

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
   return True 

if __name__ == "__main__":
   main(sys.argv[1:])
