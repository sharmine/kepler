#!/bin/bash
for var in $(seq 1 $1)
do
java -jar -Xms2048m -Xmx2048m -XX:NewSize=512m -XX:PermSize=512m -server -XX:+DisableExplicitGC -verbose:gc -XX:+PrintGCDateStamps -XX:+PrintGCDetails $2 >>log$var.log &
sleep 2s
done