package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.hephaestus.relentlessdead.impl.entitest.PathfindingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathNodeNavigator {
   private final PathNode[] successors = new PathNode[32];
   private final int range;
   private final PathNodeMaker pathNodeMaker;
   private final PathMinHeap minHeap = new PathMinHeap();

   public PathNodeNavigator(PathNodeMaker pathNodeMaker, int range) {
      this.pathNodeMaker = pathNodeMaker;
      this.range = range;
   }

   @Nullable
   public Path findPathToAny(ChunkCache world, PathfindingEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
      this.minHeap.clear();
      this.pathNodeMaker.init(world, mob);
      PathNode pathNode = this.pathNodeMaker.getStart();
      Map<TargetPathNode, BlockPos> map = positions.stream().collect(Collectors.toMap((blockPos) ->
           this.pathNodeMaker.getNode(blockPos.getX(), blockPos.getY(), (double)blockPos.getZ()), Function.identity()));
      Path path = this.findPathToAny(pathNode, map, followRange, distance, rangeMultiplier);
      this.pathNodeMaker.clear();
      return path;
   }

   @Nullable
   private Path findPathToAny(PathNode startNode, Map<TargetPathNode, BlockPos> targets, float followRange, int distance, float rangeMultiplier) {
      Set<TargetPathNode> set = targets.keySet();
      startNode.penalizedPathLength = 0.0F;
      startNode.distanceToNearestTarget = this.calculateDistances(startNode, set);
      startNode.heapWeight = startNode.distanceToNearestTarget;
      this.minHeap.clear();
      this.minHeap.push(startNode);
      int i = 0;
      Set<TargetPathNode> set3 = Sets.newHashSetWithExpectedSize(set.size());
      int j = (int)((float)this.range * rangeMultiplier);

      while(!this.minHeap.isEmpty()) {
         ++i;
         if (i >= j) {
            break;
         }

         PathNode pathNode = this.minHeap.pop();
         pathNode.visited = true;

         for (TargetPathNode targetPathNode : set) {
            if (pathNode.getManhattanDistance(targetPathNode) <= (float) distance) {
               targetPathNode.markReached();
               set3.add(targetPathNode);
            }
         }

         if (!set3.isEmpty()) {
            break;
         }

         if (pathNode.getDistance(startNode) < followRange) {
            int k = this.pathNodeMaker.getSuccessors(this.successors, pathNode);

            for(int l = 0; l < k; ++l) {
               PathNode pathNode2 = this.successors[l];
               float f = pathNode.getDistance(pathNode2);
               pathNode2.pathLength = pathNode.pathLength + f;
               float g = pathNode.penalizedPathLength + f + pathNode2.penalty;
               if (pathNode2.pathLength < followRange && (!pathNode2.isInHeap() || g < pathNode2.penalizedPathLength)) {
                  pathNode2.previous = pathNode;
                  pathNode2.penalizedPathLength = g;
                  pathNode2.distanceToNearestTarget = this.calculateDistances(pathNode2, set) * 1.5F;
                  if (pathNode2.isInHeap()) {
                     this.minHeap.setNodeWeight(pathNode2, pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget);
                  } else {
                     pathNode2.heapWeight = pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget;
                     this.minHeap.push(pathNode2);
                  }
               }
            }
         }
      }

      Optional<Path> optional = !set3.isEmpty() ? set3.stream().map((targetPathNodex) -> {
         return this.createPath(targetPathNodex.getNearestNode(), targets.get(targetPathNodex), true);
      }).min(Comparator.comparingInt(Path::getLength)) : set.stream().map((targetPathNodex) -> {
         return this.createPath(targetPathNodex.getNearestNode(), targets.get(targetPathNodex), false);
      }).min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
      if (!optional.isPresent()) {
         return null;
      } else {
         Path path = optional.get();
         return path;
      }
   }

   private float calculateDistances(PathNode node, Set<TargetPathNode> targets) {
      float f = Float.MAX_VALUE;

      float g;
      for(Iterator var4 = targets.iterator(); var4.hasNext(); f = Math.min(g, f)) {
         TargetPathNode targetPathNode = (TargetPathNode)var4.next();
         g = node.getDistance(targetPathNode);
         targetPathNode.updateNearestNode(g, node);
      }

      return f;
   }

   private Path createPath(PathNode endNode, BlockPos target, boolean reachesTarget) {
      List<PathNode> list = Lists.newArrayList();
      PathNode pathNode = endNode;
      list.add(0, endNode);

      while(pathNode.previous != null && !list.contains(pathNode.previous)) {
         pathNode = pathNode.previous;
         list.add(0, pathNode);
      }

      return new Path(list, target, reachesTarget);
   }
}
