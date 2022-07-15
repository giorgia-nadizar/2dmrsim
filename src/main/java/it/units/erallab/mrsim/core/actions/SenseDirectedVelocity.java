/*
 * Copyright 2022 Eric Medvet <eric.medvet@gmail.com> (as eric)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.units.erallab.mrsim.core.actions;

import it.units.erallab.mrsim.core.ActionPerformer;
import it.units.erallab.mrsim.core.Agent;
import it.units.erallab.mrsim.core.SelfDescribedAction;
import it.units.erallab.mrsim.core.bodies.Body;
import it.units.erallab.mrsim.core.geometry.Point;
import it.units.erallab.mrsim.engine.ActionException;
import it.units.erallab.mrsim.util.DoubleRange;

public record SenseDirectedVelocity(double direction, Body body) implements Sense<Body>, SelfDescribedAction<Double> {
  @Override
  public DoubleRange range() {
    return DoubleRange.UNBOUNDED;
  }

  @Override
  public Double perform(ActionPerformer performer, Agent agent) throws ActionException {
    Point v = body.centerLinearVelocity();
    double a = v.direction() - direction;
    return v.magnitude() * Math.cos(a);
  }
}
