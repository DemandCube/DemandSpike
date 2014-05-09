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
 
# Install kafka 
- Download kafka
```
wget https://www.apache.org/dyn/closer.cgi?path=/kafka/0.8.1.1/kafka_2.8.0-0.8.1.1.tgz
tar xvzf kafka_2.8.0-0.8.1.1.tgz
```
- Start kafka and zookeeper
```
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```
- Create topic named test, later we will send data to the test topic.
```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```
- Start a consumer, to see data coming to the test topic.
```
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning
```

# Build the application
- Open DemandSpike/src/main/resources/config.yml, to configure the kafka producer, if you had installed kafka with default parameters then you don't have to change the config.yml, You can run the random producer like a single java program without using yarn by excuting gradle runRamdomProducer.

- Execute gradle allInOne, this will generate 2 jar, we will use them later.

# Run the application
- Install Hodoop Yarn (details are here https://github.com/DemandCube/yarn-app)
- Run Hadoop Yarn
```
$HADOOP_PREFIX/bin/hdfs namenode -format
$HADOOP_PREFIX/sbin/hadoop-daemon.sh start namenode
$HADOOP_PREFIX/sbin/hadoop-daemon.sh start datanode
$HADOOP_PREFIX/sbin/yarn-daemon.sh start resourcemanager
$HADOOP_PREFIX/sbin/yarn-daemon.sh start nodemanager
```
- Copy both jars from DemandSpike/build/libs to Hadoop root folder
- Go to $HADOOP_PREFIX and copy jars to hdfs.
```
$HADOOP_PREFIX/bin/hdfs dfs -copyFromLocal DemandSpike.jar /
$HADOOP_PREFIX/bin/hdfs dfs -copyFromLocal DemandSpike-standalone.jar /
```
- Start the application on yarn 
```
$HADOOP_PREFIX/bin/hadoop jar DemandSpike.jar com.demandcube.demandspike.yarn.Client -am_mem 300 -container_mem 300 --container_cnt 4 --hdfsjar /DemandSpike.jar --app_name foobar --command "echo" --am_class_name "com.demandcube.demandspike.yarn.SampleAM"
```
Take a look at the kafka consumer, you will see coming messages

