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

package io.github.ericmedvet.mrsim2d.core.engine;

import io.github.ericmedvet.mrsim2d.core.Action;

/**
 * @author "Eric Medvet" on 2022/07/07 for 2dmrsim
 */
public class ActionException extends Exception {
  private final Action<?> action;

  public ActionException(Action<?> action, String cause) {
    super(String.format("Cannot perform action %s: %s", action, cause));
    this.action = action;
  }

  public ActionException(String message) {
    super(message);
    this.action = null;
  }

  public Action<?> getAction() {
    return action;
  }

}
