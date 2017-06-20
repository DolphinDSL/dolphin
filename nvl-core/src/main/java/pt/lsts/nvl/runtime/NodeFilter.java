package pt.lsts.nvl.runtime;


import java.util.function.Function;

public final class NodeFilter {

  private String requiredType;
  private String requiredId;
  private Payload requiredPayload;
  private Area requiredRegion;

  public NodeFilter() {
    requiredId = null;
    requiredPayload = null;
    requiredRegion = null;
  }


  public boolean matchedBy(Node v) {
    return (requiredId == null || v.getId().matches(requiredId))
        && (requiredType == null || v.getType().equals(requiredType))
        && ((requiredPayload == null || v.getPayload().compatibleWith(requiredPayload)))
        && ((requiredRegion == null || requiredRegion.contains(v.getPosition())));
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
  public Payload getRequiredPayload() {
    return requiredPayload;
  }

  /**
   * @param requiredPayload the requiredPayload to set
   */
  public void setRequiredPayload(Payload requiredPayload) {
    this.requiredPayload = requiredPayload;
  }


  /**
   * @param a Region to set
   */
  public void setRegion(Area a) {
    this.requiredRegion = a;
  }

  public Area getRegion() {
    return requiredRegion;
  }


  @Override
  public String toString() {
    Function<Object, String> str = x -> (x != null ? x.toString() : "<null>");

    return String.format("id=%s,type=%s,payload=%s,area=%s",
        str.apply(requiredId), 
        str.apply(requiredType),
        str.apply(requiredPayload),
        str.apply(requiredRegion) );
  }
}
