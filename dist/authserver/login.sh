#!/bin/bash

java_opts=-Xms1G
java_opts="$java_opts -Xmx1G"

java_settings="$java_settings -Dfile.encoding=UTF-8"
java_settings="$java_settings -Djava.net.preferIPv4Stack=true"

while :;
do
	java -server $java_settings $java_opts -cp config:./../lib/*:authserver.jar l2s.authserver.AuthServer > log/stdout.log 2>&1
	[ $? -ne 2 ] && break
	sleep 10;
done