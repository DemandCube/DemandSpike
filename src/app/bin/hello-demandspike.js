function install() {
  cluster.module.list({
    params: {"type": "available" },

    onResponse: function(resp) {
      console.h1("List the current module status of all the servers") ;
      for(var i = 0; i < resp.results.length; i++) {
        var result = resp.results[i];
        var printer = new ModuleRegistrationPrinter(console, result.fromMember, result.result);
        printer.printModuleRegistration() ;
      }
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  var installResponseHandler = function(resp) {
    for(var i = 0; i < resp.results.length; i++) {
      var result = resp.results[i];
      var printer = new ModuleRegistrationPrinter(console, result.fromMember, result.result);
      printer.printModuleRegistration() ;
    }
    Assert.assertTrue(resp.success && !resp.isEmpty()) ;
  };

  var installParams = [
    { 
      "member-role": "zookeeper",  "autostart": true, "module": ["Zookeeper"],
      "-Pmodule.data.drop": "true" ,
      "-Pzk:clientPort": "2181"
    },
    { 
      "member-role": "kafka",  "autostart": true, "module": ["Kafka"],
      "-Pmodule.data.drop": "true" ,
      "-Pkafka:port": "9092", "-Pkafka:zookeeper.connect": "127.0.0.1:2181",
      "-Pkafka.zookeeper-urls": "127.0.0.1:2181"
    },
    { 
      "member-role": "sparkngin",  "autostart": true, "module": ["Sparkngin"],
      "-Pmodule.data.drop": "true" 
    },
    { 
      "member-role": "demandspike",  "autostart": true, "module": ["DemandSpike"],
      "-Pmodule.data.drop": "true" 
    }
  ];

  for(var i = 0; i < installParams.length; i++) {
    var params = installParams[i];
    cluster.module.install({
      params: params,

      onResponse: function(resp) {
        console.h1("Install the module " + params["module"] + ", target the server(s) with the " + params['member-role'] + " role") ;
        installResponseHandler(resp) ;
      }
    }) ;
  }

  java.lang.Thread.sleep(1000) ;
}

function uninstall() {
  var uninstallResponseHandler = function(resp) {
    for(var i = 0; i < resp.results.length; i++) {
      var result = resp.results[i];
      var printer = new ModuleRegistrationPrinter(console, result.fromMember, result.result);
      printer.printModuleRegistration() ;
    }
    Assert.assertTrue(resp.success && !resp.isEmpty()) ;
  };

  var uninstallParams = [
    { "member-role": "demandspike",  "module": ["DemandSpike"], "timeout": 20000 },
    { "member-role": "sparkngin",  "module": ["Sparkngin"], "timeout": 20000 },
    { "member-role": "kafka",      "module": ["Kafka"],     "timeout": 20000 },
    { "member-role": "zookeeper",  "module": ["Zookeeper"], "timeout": 20000 }
  ];
  for(var i = 0; i < uninstallParams.length; i++) {
    var params = uninstallParams[i] ;
    cluster.module.uninstall({
      params: params,

      onResponse: function(resp) {
        console.h1("Uninstall the module " + params['module'] + ", target the server(s) with the " + params['member-role'] + " role") ;
        uninstallResponseHandler(resp) ;
      }
    }) ;
  }
  cluster.module.list({
    params: {"type": "available" },

    onResponse: function(resp) {
      console.println("List the available modules") ;
      for(var i = 0; i < resp.results.length; i++) {
        var result = resp.results[i];
        var printer = new ModuleRegistrationPrinter(console, result.fromMember, result.result);
        printer.printModuleRegistration() ;
      }
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;
}

function submitDemandSpikeJob(profile) {
  var params = {
    "driver": "kafka", "connect-url": "127.0.0.1:9092", "topic": "metrics.consumer", 
    "num-of-task": 2,  "num-of-thread": 2, "message-size": profile.messageSize,
    "member-role": "demandspike", "max-duration": profile.maxDuration, "max-num-of-message": 3000000
  };

  cluster.plugin('demandspike','submit', {
    params: params,
    onResponse: function(resp) {
      console.h1("Submit a demandspike job");
      new ResponsePrinter(console, resp).print();
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  });

  console.h1("Wait for " + profile.maxDuration + "ms");
  java.lang.Thread.sleep(profile.maxDuration);

  cluster.server.metric({
    params: {  },

    onResponse: function(resp) {
      for(var i = 0; i < resp.results.length; i++) {
        var result = resp.results[i];
        var printer = new MetricPrinter(console, result.fromMember, result.result);
        printer.printTimer();
      }
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;

  cluster.server.clearMetric({
    params: {"expression": "*Kafka*" },

    onResponse: function(resp) {
      console.h1("Remove *Kafka* metric monitor") ;
      new ResponsePrinter(console, resp).print() ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;
}

function exit() {
  cluster.server.exit({
    params: { },

    onResponse: function(resp) {
      console.h1("Shutdown the cluster") ;
      new ResponsePrinter(console, resp).print() ;
      Assert.assertTrue(resp.success && !resp.isEmpty()) ;
    }
  }) ;
}

try {
  var maxDuration = 300000 ;
  var profiles = [
    {
      demandspike: {
        messageSize: 1024, 
        maxDuration: maxDuration,
      }
    },
    {
      demandspike: {
        messageSize: 2048, 
        maxDuration: maxDuration
      }
    }
  ]
  for(var i = 0; i < profiles.length; i++) {
    var profile = profiles[i];
    install() ;
    submitDemandSpikeJob(profile.demandspike);
    uninstall() ;
    java.lang.Thread.sleep(2000);
  }
  exit() ;
} catch(error) {
  console.printError(error) ;
  throw error ;
}
