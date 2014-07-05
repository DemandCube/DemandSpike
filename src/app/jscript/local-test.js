ScriptRunner.require("cluster/ZookeeperCluster.js");
ScriptRunner.require("cluster/KafkaCluster.js");
ScriptRunner.require("cluster/SparknginCluster.js");
ScriptRunner.require("cluster/DemandSpikeCluster.js");

this.KAFKA_CONFIG = {
  port: 9092, 
  zookeeperConnect: "127.0.0.1:2181",
  serverRole: "kafka", 
  servers: ["kafka1", "kafka2", "kafka3"],
  kafkaConnect: "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
};

this.SPARKNGIN_CONFIG = {
  serverRole: "sparkngin", 
  servers: ["sparkngin1"],
  httpListenPort: 8080,
  forwarderClass: "com.neverwinterdp.sparkngin.http.NullDevMessageForwarder",
  sparknginConnect: "127.0.0.1:8080"
};

var ClusterEnv = {
  zkCluster: new ZookeeperCluster() ,
  kafkaCluster: new KafkaCluster(KAFKA_CONFIG),
  sparknginCluster: new SparknginCluster(SPARKNGIN_CONFIG),
  demandspikeCluster: new DemandSpikeCluster(),

  install: function() {
    this.zkCluster.installByServer() ;
    this.kafkaCluster.installByServer() ;
    this.sparknginCluster.installByServer() ;
    this.demandspikeCluster.installByServer() ;
    ClusterShell.module.list() ;
  },

  uninstall: function() {
    this.demandspikeCluster.uninstall() ;
    this.sparknginCluster.uninstall() ;
    this.kafkaCluster.uninstall() ;
    this.zkCluster.uninstall() ;
  }
}

function runAll() {
  var demandSpikeJobParams = [
    {
      //test kafka with 1k message size
      "driver": "kafka", "broker-connect": KAFKA_CONFIG.kafkaConnect, "topic": "metrics.consumer", 
      "num-of-task": 2,  "num-of-thread": 2, "message-size": 1024,
      "member-role": "demandspike", "max-duration": 15000, "max-num-of-message": 3000000
    },
    {
      //test kafka with 2k message size
      "driver": "kafka", "broker-connect": KAFKA_CONFIG.kafkaConnect, "topic": "metrics.consumer", 
      "num-of-task": 2,  "num-of-thread": 2, "message-size": 2048,
      "member-role": "demandspike", "max-duration": 15000, "max-num-of-message": 3000000
    },
    {
      //test sparkngin with 1k message size, forward to device null
      "driver": "sparkngin", "broker-connect": SPARKNGIN_CONFIG.sparknginConnect, "topic": "metrics.consumer", 
      "num-of-task": 2,  "num-of-thread": 2, "message-size": 1024,
      "member-role": "demandspike", "max-duration": 15000, "max-num-of-message": 3000000
    }
  ]

  for(var i = 0; i < demandSpikeJobParams.length; i++) {
    var jobParams = demandSpikeJobParams[i] ;
    ClusterEnv.install() ;
    ClusterEnv.demandspikeCluster.submitDemandSpikeJob(jobParams, true) ;
    ClusterShell.server.metric({}) ;
    ClusterShell.server.clearMetric({"expression": "*"}) ;
    ClusterEnv.uninstall() ;
  }
}

function runSingle() {
  var jobParams = {
    //test kafka with 1k message size
    "driver": "kafka", "broker-connect": KAFKA_CONFIG.kafkaConnect, "topic": "metrics.consumer", 
    "num-of-task": 2,  "num-of-thread": 2, "message-size": 1024,
    "member-role": "demandspike", "max-duration": 60000, "max-num-of-message": 3000000
  }

  ClusterEnv.install() ;
  ClusterEnv.demandspikeCluster.submitDemandSpikeJob(jobParams, true) ;
  ClusterShell.server.metric({}) ;
  ClusterShell.server.clearMetric({"expression": "*"}) ;
  ClusterEnv.uninstall() ;
}

function runKafkaRandomFailure() {
  var jobParams = {
    //test kafka with 1k message size
    "driver": "kafka", "broker-connect": KAFKA_CONFIG.kafkaConnect, "topic": "metrics.consumer", 
    "num-of-task": 2,  "num-of-thread": 2, "message-size": 1024,
    "member-role": "demandspike", "max-duration": 60000, "max-num-of-message": 3000000,

    "-Problem:kafka.problem": "service-failure",
    "-Problem:kafka.member-role": "kafka",
    "-Problem:kafka.module": "Kafka",
    "-Problem:kafka.service-id": "KafkaClusterService",
    "-Problem:kafka.period": "15000",
    "-Problem:kafka.failure-time": "1000"
  }

  ClusterEnv.install() ;
  ClusterEnv.demandspikeCluster.submitDemandSpikeJob(jobParams, true) ;
  ClusterShell.server.metric({}) ;
  ClusterShell.server.clearMetric({"expression": "*"}) ;
  ClusterEnv.uninstall() ;
}

runSingle() ;
ClusterShell.server.clearMetric({"expression": "*"}) ;
runKafkaRandomFailure() ;
//runAll() ;

