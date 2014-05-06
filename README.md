DemandSpike
===========

Load Testing Framework for Distributed Applications built on Top of Yarn

# Levels of aggregation
- Global Totals
- By Machine
- By Container

# Data points to measure

- Volume (number of events)
- IO (Size of the data being sent)
- Latency (Time to send an event)
- Error (Number of error or failures)
- 
# How to run DemandSpike

Install kafka 
- download kafka
```
wget https://www.apache.org/dyn/closer.cgi?path=/kafka/0.8.1.1/kafka_2.8.0-0.8.1.1.tgz
tar xvzf kafka_2.8.0-0.8.1.1.tgz
```
- start kafka and zookeepe
```
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```
create topic, here the name of the topic is test, later we will send data to the test topic.
```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```
-Start a consumer, to see data coming to the test topic.
```
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning
```

Run the application
```
- Open DemandSpike/src/main/resources/config.yml, to configure the kafka producer
```
period: 1
maxSize: 255
ip: localhost
port: 9092
topic_name: test
```

You can run the random producer like a single java program without using yarn by excuting gradle runRamdomProducer.

- Install Hodoop Yarn (details are here https://github.com/DemandCube/yarn-app)
- execute gradle allInOne
- Copy both jars from build/libs to Hadoop root folder
- Copy jars to hdfs.
```
$HADOOP_PREFIX/bin/hdfs dfs -copyFromLocal DemandSpike.jar /
$HADOOP_PREFIX/bin/hdfs dfs -copyFromLocal DemandSpike-standalone.jar /
```
- Start the application on yarn 
```
$HADOOP_PREFIX/bin/hadoop jar DemandSpike.jar com.demandcube.yarn.Client -am_mem 300 -container_mem 300 --container_cnt 4 --hdfsjar /DemandSpike.jar --app_name foobar --command "echo" --am_class_name "com.demandcube.yarn.SampleAM"
```

