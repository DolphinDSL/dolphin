package pt.lsts.nvl.runtime.tasks;

import java.util.Collections;
import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;

public class PlatformTask implements Task {

  private final String id;
  
  public PlatformTask(String id) {
    this.id = id;
  }
  
  @Override
  public String getId() {
    return id;
  }

  @Override
  public List<VehicleRequirements> getRequirements() {
    return Collections.emptyList();
  }
  
  @Override
  public TaskExecutor getExecutor() {
    // TODO Auto-generated method stub
    return null;
  }


}
