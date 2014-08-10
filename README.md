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


#Current Design And Implementation#

DemandSpike is designed as a command of the cluster shell. The demandspike command has several subcommand and it can be used with the existing cluster command to create a powerful test script.

![DemandSpike Architecture](diagrams/images/demandspike_architecture.png?raw=true "DemandSpike Architecture")
 
##DemandSpike Command##

Send Command

```
demandspike:job send --max-num-of-message 1000

demandspike:job send 
  --driver kafka --broker-connect 127.0.0.1:9092 
  --topic metrics.consumer --max-num-of-message 1000
```

service failure simulation command

```
 
demandspike:job simulation
  --name service-failure --target-member-role kafka  
  --module Kafka --service-id KafkaClusterService --delay 0 --period 5000 --failure-time 1000

```

TODO: Implement a send command that start an app master on yarn to create a load test for kafka and sparkngin

##DemandSpike Job##

DemandSpike job is a test script that can be queued and run by the job scheduler. A demandspike job has the main attributes such job id, job description , the script to run. When a job is finished , the job runner can store some information such the output, the cluster metrics... with the demandspike job. 

To install the DemandSpike Job Service

```
module install --member-role demandspike --autostart --module DemandSpike
```

To submit a job to the job service

```
demandspike submit --member-name demandspike --file demandspikejob.json

where the demandspikejob.json

{
  "id":   "1",
  "description": "Sample DemandSpike job",

  "tasks": [
    {
      "description": "clean metric task",
      "command":     "server metric-clear --expression *"
    },
    {
      "description": "send by the dummy driver",
      "command":     "demandspike:job send --max-num-of-message 1000"
    },
    {
      "description": "Run service failure simulation",
      "command":     "demandspike:job simulation --name service-failure --target-member-role kafka --module Kafka --service-id KafkaClusterService --delay 3000" 
    },
    {
      "description": "send by the kafka driver",
      "command":     "demandspike:job send --driver kafka --broker-connect 127.0.0.1:9092 --topic metrics.consumer --max-num-of-message 1000"
    }
  ]
}
```

To get the job service scheduler info

```
demandspike scheduler --member-name demandspike
```

To uninstall the demandspike job service

```
module uninstall --member-role demandspike --timeout 20000 --module DemandSpike
```

To submit a demandspike job to the job service

#Build And Develop#

##Build With Gradle##

1. cd Sparkngin
2. gradle clean build install

##Eclipse##

To generate the eclipse configuration

1. cd path/DemandSpike
2. gradle eclipse

To import the project into the  eclipse

1. Choose File > Import
2. Choose General > Existing Projects into Workspace
3. Check Select root directory and browse to path/DemandSpike
4. Select all the projects then click Finish

#Run DemandSpike job
Build the release
```
gradle clean build install release
```
Start demandSpike job
```
cd build/release/DemandSpike/bin
#To launch the server
./server.sh 
#Ping to check the server status
./shell.sh -c server ping 
#To launch the batch script tets
./shell.sh -f  hello-demandspike.csh
```
