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

import io.github.ericmedvet.mrsim2d.core.Action;
import io.github.ericmedvet.mrsim2d.core.ActionOutcome;
import io.github.ericmedvet.mrsim2d.viewer.ComponentDrawer;

import java.awt.*;

/**
 * @author "Eric Medvet" on 2022/07/15 for 2dmrsim
 */
public abstract class AbstractActionComponentDrawer<A extends Action<O>, O> implements ComponentDrawer {
  private final Class<A> actionClass;

  public AbstractActionComponentDrawer(Class<A> actionClass) {
    this.actionClass = actionClass;
  }

  protected abstract boolean innerDraw(double t, ActionOutcome<A, O> actionOutcome, Graphics2D g);

  @SuppressWarnings("unchecked")
  @Override
  public boolean draw(double t, Object component, Graphics2D g) {
    if (component instanceof ActionOutcome<?, ?> actionOutcome) {
      if (actionClass.isAssignableFrom(actionOutcome.action().getClass())) {
        return innerDraw(t, (ActionOutcome<A, O>) actionOutcome, g);
      }
    }
    return false;
  }
}
