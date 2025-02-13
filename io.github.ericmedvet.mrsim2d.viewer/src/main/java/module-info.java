/**
 * @author "Eric Medvet" on 2022/08/28 for 2dmrsim
 */
module io.github.ericmedvet.mrsim2d.viewer {
  requires jcodec;
  requires java.desktop;
  requires java.logging;
  requires io.github.ericmedvet.mrsim2d.core;
  requires io.github.ericmedvet.jsdynsym.core;
  exports io.github.ericmedvet.mrsim2d.viewer;
  exports io.github.ericmedvet.mrsim2d.viewer.drawers;
  exports io.github.ericmedvet.mrsim2d.viewer.drawers.actions;
  exports io.github.ericmedvet.mrsim2d.viewer.drawers.bodies;
  exports io.github.ericmedvet.mrsim2d.viewer.framers;
}