package io.github.ericmedvet.mrsim2d.core.actions;

import io.github.ericmedvet.jsdynsym.core.DoubleRange;
import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import io.github.ericmedvet.mrsim2d.core.geometry.Point;

/**
 * @author "Eric Medvet" on 2023/01/21 for 2dmrsim
 */
public record SenseNFC(
    Body body,
    Point displacement,
    double direction,
    short channel
) implements Sense<Body> {
  @Override
  public Body body() {
    return body;
  }

  @Override
  public DoubleRange range() {
    return DoubleRange.SYMMETRIC_UNIT;
  }

}
