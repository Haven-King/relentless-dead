package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

public class TargetPathNode extends PathNode {
   private float nearestNodeDistance = Float.MAX_VALUE;
   private PathNode nearestNode;
   private boolean reached;

   public TargetPathNode(PathNode node) {
      super(node.x, node.y, node.z);
   }

   public void updateNearestNode(float distance, PathNode node) {
      if (distance < this.nearestNodeDistance) {
         this.nearestNodeDistance = distance;
         this.nearestNode = node;
      }

   }

   public PathNode getNearestNode() {
      return this.nearestNode;
   }

   public void markReached() {
      this.reached = true;
   }
}
