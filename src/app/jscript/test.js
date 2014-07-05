ScriptRunner.require("cluster/ZookeeperCluster.js");
ScriptRunner.require("cluster/KafkaCluster.js");
ScriptRunner.require("cluster/SparknginCluster.js");
ScriptRunner.require("cluster/DemandSpikeCluster.js");

this.KAFKA_CONFIG = {
  port: 9092, 
  zookeeperConnect: "127.0.0.1:2181",
  serverRole: "kafka", 
  servers: ["kafka1", "kafka2"],
  kafkaConnect: "127.0.0.1:9092,127.0.0.1:9093"
};

var ClusterEnv = {
  zkCluster: new ZookeeperCluster() ,
  kafkaCluster: new KafkaCluster(KAFKA_CONFIG),
  sparknginCluster: new SparknginCluster(),
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

var demandSpikeJobParams = [
  {
    "driver": "kafka", "broker-connect": KAFKA_CONFIG.kafkaConnect, "topic": "metrics.consumer", 
    "num-of-task": 2,  "num-of-thread": 2, "message-size": 1024,
    "member-role": "demandspike", "max-duration": 15000, "max-num-of-message": 3000000
  },
  {
    "driver": "kafka", "broker-connect": KAFKA_CONFIG.kafkaConnect, "topic": "metrics.consumer", 
    "num-of-task": 2,  "num-of-thread": 2, "message-size": 2048,
    "member-role": "demandspike", "max-duration": 15000, "max-num-of-message": 3000000
  }
]

ClusterEnv.install() ;
ClusterEnv.demandspikeCluster.submitDemandSpikeJob(demandSpikeJobParams[0], true) ;
ClusterShell.server.metric({}) ;
ClusterShell.server.clearMetric({"expression": "*"}) ;
ClusterEnv.uninstall() ;
