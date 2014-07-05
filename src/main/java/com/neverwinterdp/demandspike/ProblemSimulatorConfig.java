package com.neverwinterdp.demandspike;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.neverwinterdp.server.gateway.CommandParams;

public class ProblemSimulatorConfig implements Serializable {
  @DynamicParameter(names = "-Problem:", description = "Problem properties")
  Map<String, String> problemProperties = new HashMap<String, String>();
  
  private Map<String, ProblemSimulator> simulators ;
  
  public Map<String, ProblemSimulator> getProblemSimulators() throws Exception {
    if(simulators == null) parse() ;
    return simulators ;
  }
  
  public void parse() throws Exception {
    SimulatorsParams simulatorProperties = new SimulatorsParams() ;
    Iterator<Map.Entry<String, String>> i = problemProperties.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<String, String> entry = i.next();
      String key = entry.getKey() ;
      int idx = key.indexOf('.') ;
      String name = key.substring(0, idx) ;
      String subKey = key.substring(idx + 1) ;
      simulatorProperties.add(name, subKey, entry.getValue());
    }
    
    simulators = new HashMap<String, ProblemSimulator>() ;
    for(Map.Entry<String, CommandParams> entry : simulatorProperties.entrySet()) {
      String name = entry.getKey() ;
      CommandParams params = entry.getValue() ;
      simulators.put(name, createSimulator(params)) ;
    }
  }
  
  ProblemSimulator createSimulator(CommandParams params) throws Exception {
    String problem = params.getString("problem") ;
    ProblemSimulator problemSimulator = null;
    if("service-failure".equals(problem)) {
      problemSimulator = new ServiceFailureSimulator() ;
    }
    if(problemSimulator != null) {
      new JCommander(problemSimulator, params.getArguments()) ;
      return problemSimulator ;
    }
    throw new Exception("Unknown problem " + problem) ;
  }
  
  static public class SimulatorsParams extends HashMap<String, CommandParams> {
    public void add(String name, String key, String value) {
      CommandParams params = get(name) ;
      if(params == null) {
        params = new CommandParams() ;
        put(name, params) ;
      }
      params.put(key, value) ;
    }
  }
}