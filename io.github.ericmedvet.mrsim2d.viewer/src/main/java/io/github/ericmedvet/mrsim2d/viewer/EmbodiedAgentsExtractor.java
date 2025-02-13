package io.github.ericmedvet.mrsim2d.viewer;

import io.github.ericmedvet.mrsim2d.core.*;
import io.github.ericmedvet.mrsim2d.core.bodies.Body;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author "Eric Medvet" on 2022/10/07 for 2dmrsim
 */
public class EmbodiedAgentsExtractor implements Function<Snapshot, List<Snapshot>> {

  private final List<EmbodiedAgent> agents;

  public EmbodiedAgentsExtractor() {
    this.agents = new ArrayList<>();
  }

  private record EmbodiedAgentSnapshot(
      Collection<ActionOutcome<?, ?>> actionOutcomes,
      Collection<Agent> agents,
      Collection<Body> bodies,
      Collection<NFCMessage> nfcMessages,
      double t
  ) implements Snapshot {}

  private static EmbodiedAgentSnapshot from(EmbodiedAgent a, Snapshot s) {
    return new EmbodiedAgentSnapshot(
        s.actionOutcomes().stream().filter(ao -> a.equals(ao.agent())).toList(),
        List.of(a),
        s.bodies().stream().filter(b -> a.bodyParts().contains(b)).toList(),
        s.nfcMessages(),
        s.t()
    );
  }

  @Override
  public List<Snapshot> apply(Snapshot snapshot) {
    //update agents memory
    snapshot.agents().stream()
        .filter(a -> a instanceof EmbodiedAgent)
        .filter(a -> !agents.contains(a))
        .forEach(a -> agents.add((EmbodiedAgent) a));
    //build snapshots
    return agents.stream()
        .map(a -> (Snapshot) from(a, snapshot))
        .toList();
  }
}
