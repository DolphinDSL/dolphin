package pt.lsts.dolphin.runtime;

public final class Area {

  private final Position center;
  private final double radius;
  
  public Area(Position center, double radius) {
    this.center = center;
    this.radius = radius;
  }
  
  public boolean contains(Position p) {
    return center.distanceTo(p) <= radius;
  }
  
  public Position getCenter() {
    return center;
  }
  
  public double getRadius() {
    return radius;
  }
  
  @Override
  public String toString() {
    return String.format("area(%s <> %f)", center, radius);
  } 
}
