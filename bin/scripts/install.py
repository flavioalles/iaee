#!/bin/bash

# iaee installation script

# jar file, configuration files and scripts are placed on a hidden folder that is created on the users home directory (/$HOME/.iaee)
# initialization scripts (iaee-master & iaee-client) are placed on $HOME/bin

CURRENT_DIR=$PWD

# create iaee hidden folder in users HOME
if [ ! -d "$HOME/.iaee/" ]
then
   echo "Creating iaee directory in $HOME..."
   sleep 1
   mkdir $HOME/.iaee
else
   echo "iaee directory already exists in $HOME"
   sleep 1
fi
# copy iaee.jar to $HOME/.iaee
echo "Copying jar file to $HOME/.iaee..."
sleep 1
cp $CURRENT_DIR/iaee.jar $HOME/.iaee/
# copy configuration file to $HOME/.iaee  
echo "Copying configuration file to $HOME/.iaee..."
sleep 1
cp $CURRENT_DIR/config.properties $HOME/.iaee/
# Creation of hidden files in the hidden directory 
echo "Creating output files (master & client) in $HOME/.iaee..."
sleep 1
if [ -f $HOME/.iaee/.master.tty ]
then
   rm $HOME/.iaee/.master.tty
fi
touch $HOME/.iaee/.master.tty
if [ -f $HOME/.iaee/.client.tty ]
then
   rm $HOME/.iaee/.client.tty
fi
touch $HOME/.iaee/.client.tty
echo "Creating control file in $HOME/.iaee..."
sleep 1
if [ -f $HOME/.iaee/.db.ctrl ] 
then
   echo "Control file already exists..."
else
   touch $HOME/.iaee/.db.ctrl 
   echo "0" > $HOME/.iaee/.db.ctrl
fi
echo "Creating pid files (master & client) in $HOME/.iaee..."
sleep 1
if [ -f $HOME/.iaee/.master.pid ]
then
   rm $HOME/.iaee/.master.pid
fi
touch $HOME/.iaee/.master.pid
if [ -f $HOME/.iaee/.client.pid ]
then
   rm $HOME/.iaee/.client.pid
fi
touch $HOME/.iaee/.client.pid
# Copy scripts to iaee's directory
if [ -d "$HOME/.iaee/iaee/" ]
then
   rm -rf $HOME/.iaee/iaee/
fi
echo "Copying scripts/ to $HOME/.iaee"
sleep 1
cp -r $CURRENT_DIR/ $HOME/.iaee/
# Move old database, if any
if [ -f "$HOME/.iaee/iaee.db" ]
then
   echo "Moving old iaee db..."
   sleep 1
   dbctrl=`cat $HOME/.iaee/.db.ctrl`
   dbctrl=$(($dbctrl+1))
   mv $HOME/.iaee/iaee.db $HOME/.iaee/iaee-$dbctrl.db
   echo $dbctrl > $HOME/.iaee/.db.ctrl
fi
# Remove SQLite journal files
if [ -f "$HOME/.iaee/iaee.db-journal" ]
then
   echo "Removing SQLite journal file..."
   sleep 1
   rm $HOME/.iaee/iaee.db-journal
fi
# Create $HOME/bin if it does not exist
if [ ! -d $HOME/bin ]
then
   echo "Creating bin directory in users home..."
   sleep 1
   mkdir $HOME/bin
fi
# copy iaee initialization scripts and management scripts to $HOME/bin
echo "Copying initialization scripts to $HOME/bin..."
sleep 1
cp $CURRENT_DIR/iaee-master $HOME/bin/
chmod u+x $HOME/bin/iaee-master
cp $CURRENT_DIR/iaee-client $HOME/bin/
chmod u+x $HOME/bin/iaee-client
echo "Copying management scripts to $HOME/bin..."
sleep 1
cp $CURRENT_DIR/iaee-kill $HOME/bin/
chmod u+x $HOME/bin/iaee-kill
cp $CURRENT_DIR/iaee-status $HOME/bin/
chmod u+x $HOME/bin/iaee-status
# Done
echo "Done."
sleep 1
