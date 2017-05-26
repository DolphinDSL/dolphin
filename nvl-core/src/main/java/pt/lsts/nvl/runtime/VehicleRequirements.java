package pt.lsts.nvl.runtime;

import java.util.List;
import java.util.function.Function;

public final class VehicleRequirements {

  private String requiredType;
  private String requiredId;
  private List<PayloadComponent> requiredPayload;
  private Position areaCenter;
  private double areaRadius;

  public VehicleRequirements() {
    requiredId = null;
    requiredPayload = null;
    areaCenter = null;
    areaRadius = 0;  
  }


  public VehicleRequirements type(String type) {
    requiredType = type;
    return this; 
  }

  public VehicleRequirements name(String name) {
    requiredId = name;
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
    return (requiredId == null || v.getId().matches(requiredId))
        && (requiredType == null || v.getType().equals(requiredType))
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
   * @param requiredId the required id to set
   */
  public void setRequiredId(String requiredId) {
    this.requiredId = requiredId;
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

    return String.format("id=%s,type=%s,payload=%s,area=%s",
        str.apply(requiredId), 
        str.apply(requiredType),
        str.apply(requiredPayload),
        str.apply(areaCenter) );
  }
}
