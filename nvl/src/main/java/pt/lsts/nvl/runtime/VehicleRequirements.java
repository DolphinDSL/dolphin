package pt.lsts.nvl.runtime;

import java.util.List;
import java.util.function.Function;

public final class VehicleRequirements {

  private String requiredType;
  private String requiredName;
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
   return     (requiredName == null || requiredName.equals(v.getId()))
           && (requiredType == null || (v.getType() == requiredType))
           && ((requiredPayload == null || v.getPayload().containsAll(requiredPayload)))
           && ((areaCenter == null || v.getPosition().near(areaCenter, areaRadius) ));
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
    Function<Object, String> str = x -> (x != null ? x.toString() : "<null>");
    
    return String.format("name=%s,type=%s,payload=%s,area=%s",
        str.apply(requiredName), 
        str.apply(requiredType),
        str.apply(requiredPayload),
        str.apply(areaCenter) );
  }




}
