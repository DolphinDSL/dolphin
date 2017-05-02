package pt.lsts.nvl.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public final class VehicleRequirements {

  private String requiredType;
  private String requiredName = null;
  private List<PayloadComponent> requiredPayload;
  private Position areaCenter;
  private double areaRadius;
  
  public VehicleRequirements() {
      requiredName = null;
      requiredPayload = null;
      areaCenter = null;
      areaRadius = 0;
      
  }


  public VehicleRequirements type(String type) {
    requiredType = type;
    return this; // for chained: http://blog.crisp.se/2013/10/09/perlundholm/another-builder-pattern-for-java
  }
  
  public VehicleRequirements name(String name) {
      requiredName = name;
      return this; 
    }

  public VehicleRequirements payload(List<PayloadComponent> components) {
    requiredPayload = components;
    return this;
  }


  /**
   * Area to cover by the vehicle
   * @param center
   * @param radius
   * @return
   */
  VehicleRequirements area(Position center, double radius) {
    areaCenter = center;
    areaRadius = radius;
    return this;
  }

  public boolean matchedBy(NVLVehicle v) {
   return     (requiredName!= null && requiredName.equals(v.getId()))
           && (requiredType != null && (v.getType() == requiredType))
           && ((requiredPayload != null && v.getPayload().containsAll(requiredPayload)))
           && ((areaCenter != null && v.getPosition().near(areaCenter, areaRadius) ));
  }

  public static List<NVLVehicle> filter(List<VehicleRequirements> reqs, List<NVLVehicle> allVehicles) {
    List<NVLVehicle> result = new ArrayList<>();
    for (VehicleRequirements req: reqs)  
        allVehicles.stream().filter(v -> req.matchedBy(v)).forEach(ok -> result.add(ok));
    return result; 
  }
  
  /**
   * @return the requiredType
   */
  public String getRequiredType() {
  	return requiredType;
  }

  /**
   * @param requiredType the requiredType to set
   */
  public void setRequiredType(String requiredType) {
  	this.requiredType = requiredType;
  }

  /**
   * @return the requiredPayload
   */
  public List<PayloadComponent> getRequiredPayload() {
  	return requiredPayload;
  }

  /**
   * @param requiredPayload the requiredPayload to set
   */
  public void setRequiredPayload(List<PayloadComponent> requiredPayload) {
  	this.requiredPayload = requiredPayload;
  }

  /**
   * @return the areaCenter
   */
  public Position getAreaCenter() {
  	return areaCenter;
  }

  /**
   * @param areaCenter the areaCenter to set
   */
  public void setAreaCenter(Position areaCenter) {
  	this.areaCenter = areaCenter;
  }

  /**
   * @return the areaRadius
   */
  public double getAreaRadius() {
  	return areaRadius;
  }

  /**
   * @param areaRadius the areaRadius to set
   */
  public void setAreaRadius(double areaRadius) {
  	this.areaRadius = areaRadius;
  }
  
  @Override
  public String toString() {
      String result="";
      if(requiredName != null)
          result+="Vehicle Name: "+requiredName+"\n";
      
      if(requiredType != null ){
          result+="Type: " + requiredType;
      }
      if(requiredPayload != null){
          result+="Payloads: \n";
          for(PayloadComponent p: requiredPayload){
            result+=p.getName()+"\n";
            if(p.getParameters()!=null){
                Iterator<String> it = p.getParameters().keySet().iterator();
                while(it.hasNext())
                    result+="\t"+it.next();
                result+="\n";
            }
          }
      }
      
      return result;
  }




}
