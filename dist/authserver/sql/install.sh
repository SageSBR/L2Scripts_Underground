#!/bin/sh

USER=root
PASS=
DBNAME=ertheia
DBHOST=localhost

for sqlfile in install/*.sql
do
        echo Loading $sqlfile ...
        mysql -h $DBHOST -u $USER --password=$PASS -D $DBNAME < $sqlfile
done