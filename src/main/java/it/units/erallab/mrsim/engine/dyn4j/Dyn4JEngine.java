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

package it.units.erallab.mrsim.engine.dyn4j;

import it.units.erallab.mrsim.core.Agent;
import it.units.erallab.mrsim.core.actions.*;
import it.units.erallab.mrsim.core.bodies.Anchor;
import it.units.erallab.mrsim.core.bodies.Anchorable;
import it.units.erallab.mrsim.core.bodies.Body;
import it.units.erallab.mrsim.core.bodies.Voxel;
import it.units.erallab.mrsim.core.geometry.Point;
import it.units.erallab.mrsim.engine.AbstractEngine;
import it.units.erallab.mrsim.engine.IllegalActionException;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import java.util.*;

/**
 * @author "Eric Medvet" on 2022/07/07 for 2dmrsim
 */
public class Dyn4JEngine extends AbstractEngine {
  private final Settings settings;
  private final World<org.dyn4j.dynamics.Body> world;

  public Dyn4JEngine(Settings settings) {
    this.settings = settings;
    world = new World<>();
    world.setSettings(settings);
  }

  public Dyn4JEngine() {
    this(new Settings());
  }

  @Override
  protected double innerTick() {
    world.step(1);
    return t() + settings.getStepFrequency();
  }

  @Override
  protected Collection<Body> getBodies() {
    return bodies;
  }

  @Override
  protected void registerActionSolvers() {
    registerActionSolver(CreateRigidBody.class, this::createRigidBody);
    registerActionSolver(CreateUnmovableBody.class, this::createUnmovableBody);
    registerActionSolver(TranslateBody.class, this::translate);
    registerActionSolver(CreateVoxel.class, this::createVoxel);
    registerActionSolver(AttachAnchor.class, this::attachAnchor);
    registerActionSolver(DetachAnchorFromAnchorable.class, this::detachAnchorFromAnchorable);
    registerActionSolver(RemoveBody.class, this::removeBody);
    super.registerActionSolvers();
  }

  private RigidBody createRigidBody(CreateRigidBody action, Agent agent) {
    RigidBody rigidBody = new RigidBody(
        action.poly(),
        action.mass(),
        RigidBody.FRICTION,
        RigidBody.RESTITUTION,
        RigidBody.LINEAR_DAMPING,
        RigidBody.ANGULAR_DAMPING
    );
    world.addBody(rigidBody.getBody());
    bodies.add(rigidBody);
    return rigidBody;
  }

  private UnmovableBody createUnmovableBody(CreateUnmovableBody action, Agent agent) {
    UnmovableBody unmovableBody = new UnmovableBody(action.poly(), UnmovableBody.FRICTION, UnmovableBody.RESTITUTION);
    world.addBody(unmovableBody.getBody());
    bodies.add(unmovableBody);
    return unmovableBody;
  }

  private Body translate(TranslateBody action, Agent agent) throws IllegalActionException {
    Point t = new Point(
        action.translation().x() - action.body().poly().boundingBox().min().x(),
        action.translation().y() - action.body().poly().boundingBox().min().y()
    );
    if (action.body() instanceof RigidBody rigidBody) {
      rigidBody.getBody().translate(t.x(), t.y());
      return rigidBody;
    }
    if (action.body() instanceof it.units.erallab.mrsim.engine.dyn4j.Voxel voxel) {
      voxel.translate(t);
      return voxel;
    }
    throw new IllegalActionException(
        action,
        String.format("Untranslatable body type: %s", action.body().getClass().getName())
    );
  }

  private Voxel createVoxel(CreateVoxel action, Agent agent) {
    it.units.erallab.mrsim.engine.dyn4j.Voxel voxel = new it.units.erallab.mrsim.engine.dyn4j.Voxel(
        action.sideLength(),
        action.mass(),
        it.units.erallab.mrsim.engine.dyn4j.Voxel.FRICTION,
        it.units.erallab.mrsim.engine.dyn4j.Voxel.RESTITUTION,
        action.softness(),
        it.units.erallab.mrsim.engine.dyn4j.Voxel.LINEAR_DAMPING,
        it.units.erallab.mrsim.engine.dyn4j.Voxel.ANGULAR_DAMPING,
        it.units.erallab.mrsim.engine.dyn4j.Voxel.VERTEX_MASS_SIDE_LENGTH_RATIO,
        action.areaRatioActiveRange(),
        it.units.erallab.mrsim.engine.dyn4j.Voxel.SPRING_SCAFFOLDINGS
    );
    Arrays.stream(voxel.getVertexBodies()).sequential().forEach(world::addBody);
    voxel.getSpringJoints().forEach(world::addJoint);
    bodies.add(voxel);
    return voxel;
  }

  private Anchor attachAnchor(AttachAnchor action, Agent agent) {
    if (action.anchor() instanceof BodyAnchor src) {
      BodyAnchor dst = action.anchorable().anchors().stream()
          .filter(a -> a instanceof BodyAnchor)
          .map(a -> (BodyAnchor) a)
          .filter(a -> !a.getJointMap().containsKey(src))
          .min(Comparator.comparingDouble(a -> a.point().distance(src.point())))
          .orElse(null);
      if (dst != null) {
        Joint<org.dyn4j.dynamics.Body> joint = new WeldJoint<>(
            src.getBody(),
            dst.getBody(),
            new Vector2(
                src.point().x(),
                src.point().y()
            )
        );
        world.addJoint(joint);
        src.getJointMap().put(dst, joint);
        dst.getJointMap().put(src, joint);
      }
      return dst;
    }
    return null;
  }

  private Collection<Anchor> detachAnchorFromAnchorable(DetachAnchorFromAnchorable action, Agent agent) {
    Collection<Anchor> removedAnchors = new ArrayList<>();
    if (action.anchor() instanceof BodyAnchor src) {
      for (Anchor dstAnchor : action.anchorable().anchors()) {
        if (dstAnchor instanceof BodyAnchor dstBodyAnchor) {
          Joint<org.dyn4j.dynamics.Body> joint = src.getJointMap().get(dstBodyAnchor);
          if (joint != null) {
            world.removeJoint(joint);
            src.getJointMap().remove(dstBodyAnchor);
            dstBodyAnchor.getJointMap().remove(src);
            removedAnchors.add(dstBodyAnchor);
          }
        }
      }
    }
    return removedAnchors;
  }

  private Body removeBody(RemoveBody action, Agent agent) throws IllegalActionException {
    //detach
    if (action.body() instanceof Anchorable anchorable) {
      perform(new DetachAllAnchors(anchorable), agent);
    }
    //remove
    if (action.body() instanceof RigidBody rigidBody) {
      world.removeBody(rigidBody.getBody());
      bodies.remove(rigidBody);
      return rigidBody;
    }
    if (action.body() instanceof it.units.erallab.mrsim.engine.dyn4j.Voxel voxel) {
      Arrays.stream(voxel.getVertexBodies()).sequential().forEach(world::removeBody);
      voxel.getSpringJoints().forEach(world::removeJoint);
      bodies.remove(voxel);
    }
    throw new IllegalActionException(
        action,
        String.format("Unsupported body type %s", action.body().getClass().getSimpleName())
    );
  }

}
