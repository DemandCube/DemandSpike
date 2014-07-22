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

# Installation
```
mkdir workspace
cd workspace
git clone https://github.com/DemandCube/NeverwinterDP-Commons
git clone https://github.com/DemandCube/Queuengin
git clone https://github.com/DemandCube/Sparkngin
git clone https://github.com/DemandCube/Scribengin
git clone https://github.com/DemandCube/Demandspike

cd NeverwinterDP-Commons
gradle clean build install

cd ../Queuengin
gradle clean build install

cd ../Sparkngin
gradle clean build install

cd ../Scribengin
gradle clean build install

cd ../Demandspike
gradle clean build release
```
# Run Job in Standalone mode
```
cd build/release/DemandSpike/bin
# launch the server
./server.sh 
# check the status
./shell.sh -c server ping 
# launch the batch script test
./shell.sh -f  hello-demandspike.csh

#To kill the servers
./bin/shell.sh -c server exit
#or
pkill -9 -f neverwinter
```
# Run Job in distributed mode using Yarn
```
cd build/release/DemandSpike/bin

./yarn-demandspike.sh
```



