package pt.lsts.nvl.runtime;


import java.util.function.Function;

public final class NodeFilter {

//  public static final class AttributeFilter<A,B> {
//    private final Function<Node,A> extractor;
//    private final BiPredicate<A,B> predicate;
//
//    private AttributeFilter(Function<Node,A> extractor, BiPredicate<A,B> pred) {
//      this.extractor = extractor;
//      this.predicate = pred;
//    }
//    
//    public boolean match(Node node, B expected) {
//      return predicate.test(extractor.apply(node), expected);
//    }
//  }
//  
//  private static final AttributeFilter<String,String> 
//     ID = new AttributeFilter<>(Node::getId, (id,re) -> id.matches(re));
//  private static final AttributeFilter<String,String> 
//     TYPE = new AttributeFilter<>(Node::getType, (a,b) -> a.equals(b));
//  private static final AttributeFilter<Payload,Payload> 
//     PAYLOAD = new  AttributeFilter<>(Node::getPayload, (a,b) -> a.compatibleWith(b));
//  private static final AttributeFilter<Position,Area>
//     AREA = new  AttributeFilter<>(Node::getPosition, (a,b) -> b.contains(a));
  
  private String requiredType;
  private String requiredId;
  private Payload requiredPayload;
  private Position areaCenter;
  private double areaRadius;

  public NodeFilter() {
    requiredId = null;
    requiredPayload = null;
    areaCenter = null;
    areaRadius = 0;  
  }


  public NodeFilter type(String type) {
    requiredType = type;
    return this; 
  }

  public NodeFilter name(String name) {
    requiredId = name;
    return this; 
  }

  public NodeFilter payload(Payload p) {
    requiredPayload = p;
    return this;
  }


  /**
   * Area to cover by the vehicle
   * @param center
   * @param radius
   * @return
   */
  NodeFilter area(Position center, double radius) {
    areaCenter = center;
    areaRadius = radius;
    return this;
  }

  public boolean matchedBy(Node v) {
    return (requiredId == null || v.getId().matches(requiredId))
        && (requiredType == null || v.getType().equals(requiredType))
        && ((requiredPayload == null || v.getPayload().compatibleWith(requiredPayload)))
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
