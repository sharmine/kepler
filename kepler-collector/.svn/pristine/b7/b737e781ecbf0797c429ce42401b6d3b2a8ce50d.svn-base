#!/bin/bash
for var in $(seq 1 $1)
do
java -jar -Dcom.kepler.connection.impl.defaultserver.timeout=8000 -Dcom.kepler.zookeeper.zkfactory.host=10.128.8.19:2181 -Dcom.kepler.service.start.xml=kepler-runtime.xml $2 >>log$var.log &
sleep 2s
done