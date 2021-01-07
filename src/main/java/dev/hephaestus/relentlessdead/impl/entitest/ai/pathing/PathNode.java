package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class PathNode {
   public final int x;
   public final int y;
   public final int z;
   private final int hashCode;
   public int heapIndex = -1;
   public float penalizedPathLength;
   public float distanceToNearestTarget;
   public float heapWeight;
   public PathNode previous;
   public boolean visited;
   public float pathLength;
   public float penalty;
   public PathNodeType type;

   @Nullable
   private Direction movementDirection = null;

   public PathNode(int x, int y, int z) {
      this.type = PathNodeTypes.BLOCKED0;
      this.x = x;
      this.y = y;
      this.z = z;
      this.hashCode = hash(x, y, z);
   }

   public PathNode copyWithNewPosition(int x, int y, int z) {
      PathNode pathNode = new PathNode(x, y, z);
      pathNode.heapIndex = this.heapIndex;
      pathNode.penalizedPathLength = this.penalizedPathLength;
      pathNode.distanceToNearestTarget = this.distanceToNearestTarget;
      pathNode.heapWeight = this.heapWeight;
      pathNode.previous = this.previous;
      pathNode.visited = this.visited;
      pathNode.pathLength = this.pathLength;
      pathNode.penalty = this.penalty;
      pathNode.type = this.type;
      return pathNode;
   }

   public static int hash(int x, int y, int z) {
      return y & 255 | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? '耀' : 0);
   }

   public float getDistance(PathNode node) {
      float f = (float)(node.x - this.x);
      float g = (float)(node.y - this.y);
      float h = (float)(node.z - this.z);
      return MathHelper.sqrt(f * f + g * g + h * h);
   }

   public float getSquaredDistance(PathNode node) {
      float f = (float)(node.x - this.x);
      float g = (float)(node.y - this.y);
      float h = (float)(node.z - this.z);
      return f * f + g * g + h * h;
   }

   public float getManhattanDistance(PathNode node) {
      float f = (float)Math.abs(node.x - this.x);
      float g = (float)Math.abs(node.y - this.y);
      float h = (float)Math.abs(node.z - this.z);
      return f + g + h;
   }

   public float getManhattanDistance(BlockPos pos) {
      float f = (float)Math.abs(pos.getX() - this.x);
      float g = (float)Math.abs(pos.getY() - this.y);
      float h = (float)Math.abs(pos.getZ() - this.z);
      return f + g + h;
   }

   public BlockPos getPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public Direction getDirection() {
      return this.movementDirection;
   }

   public void setDirection(Direction dir) {
      this.movementDirection = dir;
   }

   public boolean equals(Object o) {
      if (!(o instanceof PathNode)) {
         return false;
      } else {
         PathNode pathNode = (PathNode)o;
         return this.hashCode == pathNode.hashCode && this.x == pathNode.x && this.y == pathNode.y && this.z == pathNode.z;
      }
   }

   public int hashCode() {
      return this.hashCode;
   }

   public boolean isInHeap() {
      return this.heapIndex >= 0;
   }

   public String toString() {
      return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
   }
}
