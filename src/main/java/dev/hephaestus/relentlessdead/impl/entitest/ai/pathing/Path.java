package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

import dev.hephaestus.relentlessdead.impl.entitest.PathfindingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class Path {
   private final List<PathNode> nodes;
   private PathNode[] field_57 = new PathNode[0];
   private PathNode[] field_55 = new PathNode[0];
   @Environment(EnvType.CLIENT)
   private Set<TargetPathNode> field_20300;
   private int currentNodeIndex;
   private final BlockPos target;
   private final float manhattanDistanceFromTarget;
   private final boolean reachesTarget;

   public Path(List<PathNode> nodes, BlockPos target, boolean reachesTarget) {
      this.nodes = nodes;
      this.target = target;
      this.manhattanDistanceFromTarget = nodes.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).getManhattanDistance(this.target);
      this.reachesTarget = reachesTarget;
   }

   public void next() {
      ++this.currentNodeIndex;
   }

   public boolean method_30849() {
      return this.currentNodeIndex <= 0;
   }

   public boolean isFinished() {
      return this.currentNodeIndex >= this.nodes.size();
   }

   @Nullable
   public PathNode getEnd() {
      return !this.nodes.isEmpty() ? this.nodes.get(this.nodes.size() - 1) : null;
   }

   public PathNode getNode(int index) {
      return this.nodes.get(index);
   }

   public void setLength(int length) {
      if (this.nodes.size() > length) {
         this.nodes.subList(length, this.nodes.size()).clear();
      }

   }

   public void setNode(int index, PathNode node) {
      this.nodes.set(index, node);
   }

   public int getLength() {
      return this.nodes.size();
   }

   public int getCurrentNodeIndex() {
      return this.currentNodeIndex;
   }

   public void setCurrentNodeIndex(int index) {
      this.currentNodeIndex = index;
   }

   public Vec3d getNodePosition(PathfindingEntity entity, int index) {
      PathNode pathNode = this.nodes.get(index);
      double d = (double)pathNode.x + (double)((int)(entity.getWidth() + 1.0F)) * 0.5D;
      double e = pathNode.y;
      double f = (double)pathNode.z + (double)((int)(entity.getWidth() + 1.0F)) * 0.5D;
      return new Vec3d(d, e, f);
   }

   public BlockPos method_31031(int i) {
      return this.nodes.get(i).getPos();
   }

   public Vec3d getNodePosition(PathfindingEntity entity) {
      return this.getNodePosition(entity, this.currentNodeIndex);
   }

   public BlockPos method_31032() {
      return this.nodes.get(this.currentNodeIndex).getPos();
   }

   public PathNode method_29301() {
      return this.nodes.get(this.currentNodeIndex);
   }

   @Nullable
   public PathNode method_30850() {
      return this.currentNodeIndex > 0 ? this.nodes.get(this.currentNodeIndex - 1) : null;
   }

   public boolean equalsPath(@Nullable Path path) {
      if (path == null) {
         return false;
      } else if (path.nodes.size() != this.nodes.size()) {
         return false;
      } else {
         for(int i = 0; i < this.nodes.size(); ++i) {
            PathNode pathNode = this.nodes.get(i);
            PathNode pathNode2 = path.nodes.get(i);
            if (pathNode.x != pathNode2.x || pathNode.y != pathNode2.y || pathNode.z != pathNode2.z) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean reachesTarget() {
      return this.reachesTarget;
   }

   public String toString() {
      return "Path(length=" + this.nodes.size() + ")";
   }

   public BlockPos getTarget() {
      return this.target;
   }

   public float getManhattanDistanceFromTarget() {
      return this.manhattanDistanceFromTarget;
   }
}
