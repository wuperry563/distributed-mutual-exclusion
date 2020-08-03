#!/bin/bash

# Change this to your netid
netid=psw101020

# Root directory of your project
PROJDIR=~/dev/CS6378/Project2

# Directory where the config file is located on your local system
CONFIGLOCAL=$HOME/dev/CS6378/Project2/config.txt

# Directory your java classes are in
BINDIR=$PROJDIR

# Your main project class
PROG=$HOME/dev/CS6378/Project2/out/node.jar

n=0
echo $CONFIGLOCAL

cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
	echo $i
    totalNodes=$(echo $i | awk '{print $1}')
    while [[ $n -lt $totalNodes ]]
    do
    	read line
    	p=$( echo $line | awk '{ print $1 }' )
        host=$( echo $line | awk '{ print $2 }' )
	
	echo $p
	echo $host
	ssh -o StrictHostKeyChecking=no $netid@$host java -jar $PROG $p $CONFIGLOCAL &

        n=$(( n + 1 ))
    done
)
