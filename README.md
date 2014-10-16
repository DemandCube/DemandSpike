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
 
Command Line for DemandSpike
============================


DemandSpike load test can be invoked by  demandspike [OPTIONS] [ARGS]

Options:
-------

run  : Used to run test. (eg: demandspike run --dest http://localhost:5070/message)

Arguments:
---------

<table width="100%" border="1" cellpadding="5">
<tr>
<th>
Argument
</th>
<th>
Value
</th>
<th>
Description
</th>
<th>
Mandatory
</th>
</tr>

<tr>
<td>
--help
</td>

<td>
-
</td>

<td>
Print usage in the screen

</td>

<td>
no
</td>

</tr>

<tr>
<td>
--name
</td>

<td>
[alphanumeric]
</td>

<td>
Name of the test. If name was not given it should take default random name like “DemandSpikeTest1”

</td>

<td>
no
</td>

</tr>

<tr>
<td>
--name
</td>

<td>
standalone
distributed
</td>

<td>
This is used to tell demandspike to run in standalone or distributed mode.
standalone - will run in single machine
distributed – will run in distributed environment (master-worker environment)
Default: standalone
</td>

<td>
no
</td>

</tr>

<tr>
<td>
--useYarn
</td>

<td>
true
<br>
false
</td>

<td>
When --mode is distributed this argument is used. It is used to say demandspike to run test using yarn or without yarn.
Default: true
</td>

<td>
no
</td>

</tr>

<tr>
<td>
--target
</td>

<td>
-
</td>

<td>
Target url.
</td>

<td>
yes
</td>

</tr>

<tr>
<td>
--cLevel
</td>

<td>
-
</td>

<td>
Concurrency level. i.e., 
number of threads for standalone mode.
number of threads on single worker machine for distributed mode.
number of container on single worker machine for yarn distributed mode.
Default : 1
</td>

<td>
no
</td>

</tr>

<tr>
<td>
--message-size
</td>

<td>
-
</td>

<td>
Size of the message in bytes. 
Default: 1024
</td>

<td>
no
</td>

</tr>

<tr>
<td>
--time
</td>

<td>
-
</td>

<td>
Test for given time period. 
Default: 300 sec
</td>

<td>
no
</td>

<tr>
<td>
--maxRequests
</td>

<td>
-
</td>

<td>
Test with maximum number of messages.
Default: 1000
</td>

<td>
no
</td>

</tr>

<tr>
<td>
--method
</td>

<td>
GET
<br>
POST
</td>

<td>
Http request method
</td>

<td>
no
</td>

</tr>

<tr>
<td>
--protocol
</td>

<td>
Http
</td>

<td>
Communication protocol
</td>

<td>
no
</td>

</tr>

<tr>
<td>
--input-data
</td>

<td>
data string
</td>

<td>
Data String to be send to the target, this data string can have some keywords enclosed with '%' character like for example %RANDOM-STRING%
Available keywords are :
%AUTO-INCREMENT-INT%
%RANDOM-STRING%

</td>

<td>
no
</td>

</tr>

<tr>
<td>
--input-file
</td>

<td>
file path
</td>

<td>
File that contains data, the content of the file is the same as input-data option value, it can have some keywords (look at input-data option for more detials)
</td>

<td>
no
</td>

</tr>

<tr>
<td>
--time
</td>

<td>
-
</td>

<td>
Test for given time period. 
Default: 300 sec
</td>

<td>
no
</td>

</tr>
</table>




Run demandspike
===============

**1. Run target**

First you need to start you target (web server , kafka, sparkngin ..), if you are testing demandspike then you can use netcat to simulate your target.

**2. build demandspike**
    
        cd DemandSpike

        gradle clean build install release
    
        cd build/release/DemandSpike/bin

**3. Submit job **

**3.1 Stand alone mode**
    
    ./demandspike run  --target .........

**3.2 Yarn mode**

3.2.1 . install hadoop

you need to install hadoop yarn 2.4, just download it from http://hadoop.apache.org/, then     export the variables below: 

    export HADOOP_COMMON_HOME=/path to hadoop
    export HADOOP_HDFS_HOME=/path to hadoop
    export HADOOP_YARN_HOME=/path to hadoop

3.2.2 start hadoop

    ./start-hadoop

3.2.3 run

    ./demandspike run  --mode distributed --useYarn true --target ........

Comandline example
==================
Target : web server
-------------------
The command below run 2 threads in standalone mode for 30 seconds to send 10000 random messages to 127.0.0.1:80/submit :
./demandspike run  --target http://127.0.0.1:80/submit --method POST --protocol HTTP --time 30000 --cLevel 2 –nMessages  10000

Target : sparkngin 
------------------
The command below run 2 thread in standalone mode for 30 seconds to send 10000  messages to sparkgin with data defined in data.json :
./demandspike run  --target http://127.0.0.1:7080/submit --method POST --protocol HTTP --time 30000 --cLevel 2 –nMessages 10000 input-file data.json

data.json content is below 


{ 
"header" : 
    {"version" : 0.0, 
     "topic" : "metrics.consumer" 
    ,"key" ": "message-sender-task-0-%AUTO-INCREMENT-INT%", 
     "traceEnable" : false, 
     "instructionEnable" : false 
    }, 

 "data": 
     {"type" : null, 
      "data" : "%RANDOM-STRING%", 
      "serializeType" : null 
     }, 

"traces" : null, 

"instructions" ": null 

} 






##Eclipse##

To generate the eclipse configuration

1. cd path/DemandSpike
2. gradle eclipse

To import the project into the  eclipse

1. Choose File > Import
2. Choose General > Existing Projects into Workspace
3. Check Select root directory and browse to path/DemandSpike
4. Select all the projects then click Finish


```
