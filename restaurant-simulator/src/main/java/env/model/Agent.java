package env.model;

public class Agent {
  private final String name;
  private Position2D position;

  public Agent(String name, Position2D position) {
    this.name = name;
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public Position2D getPosition() {
    return position;
  }

  public void setPosition(Position2D position) {
    this.position = position;
  }


}
