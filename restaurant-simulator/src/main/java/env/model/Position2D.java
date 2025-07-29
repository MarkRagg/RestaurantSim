package env.model;

import java.util.Objects;

public class Position2D {
  private int x;
  private int y;

  public Position2D(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static Position2D of(int x, int y) {
      return new Position2D(x, y);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Position2D)) return false;
    Position2D that = (Position2D) o;
    return x == that.x && y == that.y;
  }

  @Override
    public int hashCode() {
        return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "Position2D{" +
            "x=" + x +
            ", y=" + y +
            '}';
  }
}
