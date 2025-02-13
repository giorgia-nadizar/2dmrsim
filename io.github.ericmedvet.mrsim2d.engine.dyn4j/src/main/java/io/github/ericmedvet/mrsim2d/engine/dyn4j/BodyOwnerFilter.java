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

package io.github.ericmedvet.mrsim2d.engine.dyn4j;

import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import org.dyn4j.collision.Filter;

/**
 * @author "Eric Medvet" on 2022/07/17 for 2dmrsim
 */
public class BodyOwnerFilter implements Filter {
  private final Body owner;

  public BodyOwnerFilter(Body owner) {
    this.owner = owner;
  }

  public Body getOwner() {
    return owner;
  }

  @Override
  public boolean isAllowed(Filter filter) {
    if (filter instanceof BodyOwnerFilter otherBodyOwnerFilter) {
      return owner != otherBodyOwnerFilter.owner;
    }
    return true;
  }
}
