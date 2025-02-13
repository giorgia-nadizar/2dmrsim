package io.github.ericmedvet.mrsim2d.core.actions;

import io.github.ericmedvet.jsdynsym.core.DoubleRange;
import io.github.ericmedvet.mrsim2d.core.Action;
import io.github.ericmedvet.mrsim2d.core.bodies.RotationalJoint;

public record CreateRotationalJoint(
    double length,
    double width,
    double mass,
    RotationalJoint.Motor motor,
    DoubleRange activeAngleRange
) implements Action<RotationalJoint> {
}
