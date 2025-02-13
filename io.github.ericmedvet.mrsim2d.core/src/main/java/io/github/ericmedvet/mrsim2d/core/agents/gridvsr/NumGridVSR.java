/*
 * Copyright 2022 eric
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

package io.github.ericmedvet.mrsim2d.core.agents.gridvsr;


import io.github.ericmedvet.jsdynsym.core.DoubleRange;
import io.github.ericmedvet.mrsim2d.core.Action;
import io.github.ericmedvet.mrsim2d.core.ActionOutcome;
import io.github.ericmedvet.mrsim2d.core.Sensor;
import io.github.ericmedvet.mrsim2d.core.actions.ActuateVoxel;
import io.github.ericmedvet.mrsim2d.core.actions.Sense;
import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import io.github.ericmedvet.mrsim2d.core.bodies.Voxel;
import io.github.ericmedvet.mrsim2d.core.util.Grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author "Eric Medvet" on 2022/07/09 for 2dmrsim
 */
public abstract class NumGridVSR extends AbstractGridVSR {

  protected final static DoubleRange INPUT_RANGE = DoubleRange.SYMMETRIC_UNIT;
  protected final static DoubleRange OUTPUT_RANGE = DoubleRange.SYMMETRIC_UNIT;

  private final Grid<List<Sensor<? super Body>>> sensorsGrid;
  private final Grid<double[]> inputsGrid;
  private final Grid<Double> outputGrid;
  private final GridBody body;

  public NumGridVSR(
      GridBody body,
      double voxelSideLength,
      double voxelMass
  ) {
    super(body.grid().map(GridBody.SensorizedElement::element), voxelSideLength, voxelMass);
    this.sensorsGrid = body.grid().map(GridBody.SensorizedElement::sensors);
    this.body = body;
    inputsGrid = body.grid().map(e -> e.element().type().equals(GridBody.VoxelType.NONE) ? null : new double[e.sensors()
        .size()]);
    outputGrid = bodyGrid.map(v -> v != null ? 0d : null);
  }

  public NumGridVSR(GridBody body) {
    this(body, VOXEL_SIDE_LENGTH, VOXEL_MASS);
  }

  protected abstract Grid<Double> computeActuationValues(double t, Grid<double[]> inputsGrid);

  @SuppressWarnings("unchecked")
  @Override
  public List<? extends Action<?>> act(double t, List<ActionOutcome<?, ?>> previousActionOutcomes) {
    //read inputs from last request
    if (!previousActionOutcomes.isEmpty()) {
      int c = 0;
      for (Grid.Key key : inputsGrid.keys()) {
        double[] inputs = inputsGrid.get(key);
        if (inputs != null) {
          for (int i = 0; i < inputs.length; i++) {
            ActionOutcome<?, ?> outcome = previousActionOutcomes.get(c);
            if (outcome.action() instanceof Sense<?>) {
              ActionOutcome<? extends Sense<Voxel>, Double> o = (ActionOutcome<? extends Sense<Voxel>, Double>) outcome;
              inputs[i] = INPUT_RANGE.denormalize(
                  o.action().range().normalize(o.outcome().orElse(0d))
              );
              c = c + 1;
            }
          }
        }
      }
    }
    //compute actuation
    computeActuationValues(t, inputsGrid).entries().forEach(e -> outputGrid.set(e.key(), OUTPUT_RANGE.clip(e.value())));
    //generate next sense actions
    List<Action<?>> actions = new ArrayList<>();
    actions.addAll(bodyGrid.entries().stream()
        .filter(e -> e.value() != null)
        .map(e -> sensorsGrid.get(e.key()).stream()
            .map(f -> f.apply(e.value()))
            .toList())
        .flatMap(Collection::stream)
        .toList());
    //generate actuation actions
    actions.addAll(bodyGrid.entries().stream()
        .filter(e -> e.value() instanceof Voxel)
        .map(e -> new ActuateVoxel((Voxel) e.value(), outputGrid.get(e.key())))
        .toList());
    return actions;
  }

  public GridBody getBody() {
    return body;
  }

}
