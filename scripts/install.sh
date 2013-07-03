#!/bin/bash

CURRENT_DIR=$PWD

# /opt/
if [ ! -d "/opt/iaee/" ]
  then
    echo "Creating iaee directory in /opt..."
    sleep 1
    sudo mkdir /opt/iaee
  else
    echo "iaee directory already exists in /opt"
    sleep 1
fi
sudo cp $CURRENT_DIR/iaee.jar /opt/iaee/
echo "ls -l /opt/iaee/"
ls -l /opt/iaee/
sleep 2

# /etc/
if [ ! -d "/etc/iaee/" ]
  then
    echo "Creating iaee directory in /etc..."
    sleep 1
    sudo mkdir /etc/iaee
  else
    echo "iaee directory already exists in /etc"
    sleep 1
fi
sudo cp $CURRENT_DIR/config.properties /etc/iaee/
echo "ls -l /etc/iaee/"
ls -l /etc/iaee/
sleep 2

# $HOME/iaee
if [ ! -d "$HOME/iaee/" ]
  then
    echo "Creating iaee directory in $HOME..."
    sleep 1
    mkdir $HOME/iaee/
    echo "Creating output files (master & client) in $HOME/iaee..."
    sleep 1
    touch $HOME/iaee/.master.tty
    touch $HOME/iaee/.client.tty
    echo "Creating control file in $HOME/iaee..."
    sleep 1
    touch $HOME/iaee/.db.ctrl 
    echo "0" > $HOME/iaee/.db.ctrl
    echo "Copying scripts/ to $HOME/iaee/"
    cp -r $CURRENT_DIR/ $HOME/iaee/
    echo "ls -l $HOME/iaee/"
    ls -la $HOME/iaee
    sleep 2
  else
    echo "iaee directory already exists in $HOME"
    sleep 1
    if [ -f "$HOME/iaee/iaee.db" ]
      then
        echo "Moving old iaee db..."
        sleep 1
        dbctrl=`cat $HOME/iaee/.db.ctrl`
        dbctrl=$(($dbctrl+1))
        mv $HOME/iaee/iaee.db $HOME/iaee/iaee-$dbctrl.db
        echo $dbctrl > $HOME/iaee/.db.ctrl
    fi
    if [ -f "$HOME/iaee/iaee.db-journal" ]
    then
      rm $HOME/iaee/iaee.db-journal
    fi
    if [ -d "$HOME/iaee/scripts/" ]
      then
        rm -rf $HOME/iaee/scripts/
    fi
    echo "Copying scripts/ to $HOME/iaee"
    cp -r $CURRENT_DIR/ $HOME/iaee/
fi

