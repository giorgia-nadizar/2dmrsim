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

package it.units.erallab.mrsim.agents;

import it.units.erallab.mrsim.core.Action;
import it.units.erallab.mrsim.core.ActionOutcome;
import it.units.erallab.mrsim.core.ActionPerformer;
import it.units.erallab.mrsim.core.EmbodiedAgent;
import it.units.erallab.mrsim.core.bodies.Body;
import it.units.erallab.mrsim.core.bodies.Voxel;
import it.units.erallab.mrsim.engine.ActionException;
import it.units.erallab.mrsim.util.Grid;

import java.util.List;
import java.util.Objects;

/**
 * @author "Eric Medvet" on 2022/07/09 for 2dmrsim
 */
public class GridVSR implements EmbodiedAgent {

  private final static double VOXEL_SIDE_LENGTH = 1d;

  private final Grid<Voxel.Material> materialGrid;
  private final double voxelSideLength;
  private final Grid<Voxel> bodyGrid;

  public GridVSR(Grid<Voxel.Material> materialGrid, double voxelSideLength) {
    this.materialGrid = materialGrid;
    this.voxelSideLength = voxelSideLength;
    bodyGrid = Grid.create(materialGrid.getW(), materialGrid.getH());
  }

  public GridVSR(Grid<Voxel.Material> materialGrid) {
    this(materialGrid, VOXEL_SIDE_LENGTH);
  }

  @Override
  public List<Action<?>> act(double t, List<ActionOutcome<?>> previousActionOutcomes) {
    return List.of();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public List<Body> bodyParts() {
    return (List)bodyGrid.values().stream().filter(Objects::nonNull).toList();
  }

  @Override
  public void assemble(ActionPerformer actionPerformer) throws ActionException {

  }
}
