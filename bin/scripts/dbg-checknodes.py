#!/usr/bin/env python
### /home/falles/bin/python2.7

import sqlite3
import sys

def main(argv):
   if len(argv) != 1:
      print('error: wrong usage.')
      returncode = 1
   else:
      try:
         conn = sqlite3.connect(argv[0])
      except ValueError:
         print('error: argument passed is not a valid database.')
         returncode = 1
      else:
         c = conn.cursor()
         try:
            for node in c.execute('SELECT * FROM Node ORDER BY NODE_NAME'):
               print node
         except sqlite3.OperationalError:
            print('sqlite3.OperationalError')
            returncode = 1
         else:
            returncode = 0
   sys.exit(returncode)

if __name__ == "__main__":
   main(sys.argv[1:])
