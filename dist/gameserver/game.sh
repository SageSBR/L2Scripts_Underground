#!/bin/bash

java_opts=-Xms4G
java_opts="$java_opts -Xmx4G"

java_settings="$java_settings -Dfile.encoding=UTF-8"
java_settings="$java_settings -Djava.net.preferIPv4Stack=true"

while :;
do
	java -server $java_settings $java_opts -cp config:./../lib/*:gameserver.jar l2s.gameserver.GameServer > log/stdout.log 2>&1
	[ $? -ne 2 ] && break
	sleep 10;
done