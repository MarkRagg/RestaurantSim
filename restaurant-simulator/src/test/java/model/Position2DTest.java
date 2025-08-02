package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import env.model.Position2D;

public class Position2DTest {

  @Test
  public void testEquals() {
    assertEquals(Position2D.of(0, 0), Position2D.of(0, 0));
    assertNotEquals(Position2D.of(0, 0), Position2D.of(1, 1));
  }
}