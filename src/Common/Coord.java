package Common;

import java.util.Objects;

/**
 * Represents a 2D coordinate on a map.
 */
public class Coord {
  public final int x;
  public final int y;

  /**
   * Constructor.
   * @param x x coordinate
   * @param y y coordinate
   */
  public Coord(int x, int y) {
    if (x < 0 | y < 0) {
      throw new IllegalArgumentException("Coordinates must be non-negative.");
    }
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Coord coord = (Coord) o;
    return x == coord.x && y == coord.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "Coord{" +
            "x=" + x +
            ", y=" + y +
            '}';
  }
}
