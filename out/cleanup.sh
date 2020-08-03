#!/bin/bash


# Change this to your netid
netid=psw101020

#
# This assumes your config file is named "config.txt"
# and is located in your project directory
#
#CONFIG=$PROJDIR/NOT-THIS-YEARS-CONFIG.txt
#CONFIG=config.txt Give it as command line argument
PROJDIR=~/dev/CS6378/Project2
n=0

cat $1 | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    
    k=$( echo $i | awk '{ print $1 }' )    
    
    echo "k=" $k
    while read line 
    do
        if [ $n -lt $k ]
	then
	host=$( echo $line | awk '{ print $2 }' )
	
	nodeId=$( echo $line | awk '{ print $3 }' )
	echo $netid "Running on" $host "with" $nodeId $n
         ssh -oStrictHostKeyChecking=no -l "$netid" "$host" "cd ; cd $PROJDIR; killall -u $netid " &
	fi
	
        n=$(( n + 1 ))
    done
   
)



