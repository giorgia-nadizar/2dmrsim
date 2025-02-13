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

package io.github.ericmedvet.mrsim2d.viewer.drawers.actions;

import io.github.ericmedvet.mrsim2d.core.ActionOutcome;
import io.github.ericmedvet.mrsim2d.core.geometry.Point;
import io.github.ericmedvet.mrsim2d.viewer.DrawingUtils;

import java.awt.*;

/**
 * @author "Eric Medvet" on 2022/07/17 for 2dmrsim
 */
public class SenseVelocity extends AbstractActionComponentDrawer<io.github.ericmedvet.mrsim2d.core.actions.SenseVelocity,
    Double> {

  private final static Color COLOR = Color.GREEN;
  private final static double MULT = 1d;

  private final Color color;

  public SenseVelocity(Color color) {
    super(io.github.ericmedvet.mrsim2d.core.actions.SenseVelocity.class);
    this.color = color;
  }

  public SenseVelocity() {
    this(COLOR);
  }

  @Override
  protected boolean innerDraw(
      double t,
      ActionOutcome<io.github.ericmedvet.mrsim2d.core.actions.SenseVelocity, Double> ao,
      Graphics2D g
  ) {
    //draw line
    g.setColor(color);
    Point src = ao.action().body().poly().center();
    Point dst = src
        .sum(new Point(ao.action().direction())
            .scale(ao.outcome().orElse(0d) * MULT));
    DrawingUtils.drawLine(g, src, dst);
    return true;
  }
}
