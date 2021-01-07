package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

import dev.hephaestus.climbable.api.ClimbingSpeedRegistry;
import dev.hephaestus.relentlessdead.impl.Dead;
import dev.hephaestus.relentlessdead.impl.entitest.PathfindingEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class LandPathNodeMaker extends PathNodeMaker {
   protected float waterPathNodeTypeWeight;
   private final Long2ObjectMap<PathNodeType> cachedNodeTypes = new Long2ObjectOpenHashMap<>();
   private final Object2BooleanMap<Box> field_25191 = new Object2BooleanOpenHashMap<>();

   public void init(ChunkCache cachedWorld, PathfindingEntity entity) {
      super.init(cachedWorld, entity);
      this.waterPathNodeTypeWeight = entity.getPathfindingPenalty(PathNodeTypes.WATER);
   }

   public void clear() {
      this.entity.setPathfindingPenalty(PathNodeTypes.WATER, this.waterPathNodeTypeWeight);
      this.cachedNodeTypes.clear();
      this.field_25191.clear();
      super.clear();
   }

   public PathNode getStart() {
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      int i = MathHelper.floor(this.entity.getY());
      BlockState blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), i, this.entity.getZ()));
      BlockPos blockPos;
      if (!this.entity.canWalkOnFluid(blockState.getFluidState().getFluid())) {
         if (this.canSwim() && this.entity.isTouchingWater()) {
            while(true) {
               if (blockState.getBlock() != Blocks.WATER && blockState.getFluidState() != Fluids.WATER.getStill(false)) {
                  --i;
                  break;
               }

               ++i;
               blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), i, this.entity.getZ()));
            }
         } else if (this.entity.isOnGround()) {
            i = MathHelper.floor(this.entity.getY() + 0.5D);
         } else {
            for(blockPos = this.entity.getBlockPos(); (this.cachedWorld.getBlockState(blockPos).isAir() || this.cachedWorld.getBlockState(blockPos).canPathfindThrough(this.cachedWorld, blockPos, NavigationType.LAND)) && blockPos.getY() > 0; blockPos = blockPos.down()) {
            }

            i = blockPos.up().getY();
         }
      } else {
         while(true) {
            if (!this.entity.canWalkOnFluid(blockState.getFluidState().getFluid())) {
               --i;
               break;
            }

            ++i;
            blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), i, this.entity.getZ()));
         }
      }

      blockPos = this.entity.getBlockPos();
      PathNodeType pathNodeType = this.getNodeType(this.entity, blockPos.getX(), i, blockPos.getZ());
      if (this.entity.getPathfindingPenalty(pathNodeType) < 0.0F) {
         Box box = this.entity.getBoundingBox();
         if (this.method_27139(mutable.set(box.minX, i, box.minZ)) || this.method_27139(mutable.set(box.minX, i, box.maxZ)) || this.method_27139(mutable.set(box.maxX, i, box.minZ)) || this.method_27139(mutable.set(box.maxX, i, box.maxZ))) {
            PathNode pathNode = this.method_27137(mutable);
            pathNode.type = this.getNodeType(this.entity, pathNode.getPos());
            pathNode.penalty = this.entity.getPathfindingPenalty(pathNode.type);
            return pathNode;
         }
      }

      PathNode pathNode2 = this.getNode(blockPos.getX(), i, blockPos.getZ());
      pathNode2.type = this.getNodeType(this.entity, pathNode2.getPos());
      pathNode2.penalty = this.entity.getPathfindingPenalty(pathNode2.type);
      return pathNode2;
   }

   private boolean method_27139(BlockPos blockPos) {
      PathNodeType pathNodeType = this.getNodeType(this.entity, blockPos);
      return this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F;
   }

   public TargetPathNode getNode(double x, double y, double z) {
      return new TargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
   }

   public int getSuccessors(PathNode[] successors, PathNode node) {
      int i = 0;
      int j = 0;
      PathNodeType feet = this.getNodeType(this.entity, node.x, node.y + 1, node.z);
      PathNodeType ground = this.getNodeType(this.entity, node.x, node.y, node.z);
      double d = getFeetY(this.cachedWorld, new BlockPos(node.x, node.y, node.z));

      if (this.entity.getPathfindingPenalty(feet) >= 0.0F && ground != PathNodeTypes.HONEY) {
         j = MathHelper.floor(Math.max(1.0F, this.entity.getStepHeight()));
      }

      if (node.type instanceof PathNodeType.Directional) {
         PathNodeType.Directional directional = (PathNodeType.Directional) node.type;
         if (!(node.type instanceof PathNodeType.Directional.Terminator)) {
            PathNode pathNode9 = this.getPathNode(node.x, node.y + 1, node.z, j, d, Direction.UP, feet);

            if (pathNode9 != null && pathNode9.type.getDefaultPenalty() >= 0 && !(pathNode9.type instanceof PathNodeType.Directional)) {
               pathNode9.type = PathNodeType.Directional.Terminator.of(directional.getDirection());
            }

            if (pathNode9 != null && pathNode9.type instanceof PathNodeType.Directional) {
               successors[i++] = pathNode9;
            }
         } else {
            int x = node.x + directional.getDirection().getOffsetX();
            int z = node.z + directional.getDirection().getOffsetZ();
            PathNode pathNode10 = this.getPathNode(x, node.y, z, j, d, node.getDirection(), feet);

            if (pathNode10 != null && pathNode10.type.getDefaultPenalty() >= 0) {
               successors[i++] = pathNode10;
            }
         }
      } else {
         PathNode pathNode = this.getPathNode(node.x, node.y, node.z + 1, j, d, Direction.SOUTH, ground);
         if (this.isValidDiagonalSuccessor(pathNode, node)) {
            successors[i++] = pathNode;
         }

         PathNode pathNode2 = this.getPathNode(node.x - 1, node.y, node.z, j, d, Direction.WEST, ground);
         if (this.isValidDiagonalSuccessor(pathNode2, node)) {
            successors[i++] = pathNode2;
         }

         PathNode pathNode3 = this.getPathNode(node.x + 1, node.y, node.z, j, d, Direction.EAST, ground);
         if (this.isValidDiagonalSuccessor(pathNode3, node)) {
            successors[i++] = pathNode3;
         }

         PathNode pathNode4 = this.getPathNode(node.x, node.y, node.z - 1, j, d, Direction.NORTH, ground);
         if (this.isValidDiagonalSuccessor(pathNode4, node)) {
            successors[i++] = pathNode4;
         }

         PathNode pathNode5 = this.getPathNode(node.x - 1, node.y, node.z - 1, j, d, Direction.NORTH, ground);
         if (this.method_29579(node, pathNode2, pathNode4, pathNode5)) {
            successors[i++] = pathNode5;
         }

         PathNode pathNode6 = this.getPathNode(node.x + 1, node.y, node.z - 1, j, d, Direction.NORTH, ground);
         if (this.method_29579(node, pathNode3, pathNode4, pathNode6)) {
            successors[i++] = pathNode6;
         }

         PathNode pathNode7 = this.getPathNode(node.x - 1, node.y, node.z + 1, j, d, Direction.SOUTH, ground);
         if (this.method_29579(node, pathNode2, pathNode, pathNode7)) {
            successors[i++] = pathNode7;
         }

         PathNode pathNode8 = this.getPathNode(node.x + 1, node.y, node.z + 1, j, d, Direction.SOUTH, ground);
         if (this.method_29579(node, pathNode3, pathNode, pathNode8)) {
            successors[i++] = pathNode8;
         }
      }

      return i;
   }

   private boolean isValidDiagonalSuccessor(PathNode node, PathNode successor1) {
      return node != null && !node.visited && (node.penalty >= 0.0F || successor1.penalty < 0.0F);
   }

   private boolean method_29579(PathNode pathNode, @Nullable PathNode pathNode2, @Nullable PathNode pathNode3, @Nullable PathNode pathNode4) {
      if (pathNode4 != null && pathNode3 != null && pathNode2 != null) {
         if (pathNode4.visited) {
            return false;
         } else if (pathNode3.y <= pathNode.y && pathNode2.y <= pathNode.y) {
            if (pathNode2.type != PathNodeTypes.WALKABLE_DOOR && pathNode3.type != PathNodeTypes.WALKABLE_DOOR && pathNode4.type != PathNodeTypes.WALKABLE_DOOR) {
               boolean bl = pathNode3.type == PathNodeTypes.FENCE && pathNode2.type == PathNodeTypes.FENCE && (double)this.entity.getWidth() < 0.5D;
               return pathNode4.penalty >= 0.0F && (pathNode3.y < pathNode.y || pathNode3.penalty >= 0.0F || bl) && (pathNode2.y < pathNode.y || pathNode2.penalty >= 0.0F || bl);
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean method_29578(PathNode pathNode) {
      Vec3d vec3d = new Vec3d((double)pathNode.x - this.entity.getX(), (double)pathNode.y - this.entity.getY(), (double)pathNode.z - this.entity.getZ());
      Box box = this.entity.getBoundingBox();
      int i = MathHelper.ceil(vec3d.length() / box.getAverageSideLength());
      vec3d = vec3d.multiply(1.0F / (float)i);

      for(int j = 1; j <= i; ++j) {
         box = box.offset(vec3d);
         if (this.method_29304(box)) {
            return false;
         }
      }

      return true;
   }

   public static double getFeetY(BlockView world, BlockPos pos) {
      BlockPos blockPos = pos.down();
      VoxelShape voxelShape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
      return (double)blockPos.getY() + (voxelShape.isEmpty() ? 0.0D : voxelShape.getMax(Direction.Axis.Y));
   }

   @Nullable
   private PathNode getPathNode(int x, int y, int z, int maxYStep, double prevFeetY, Direction direction, PathNodeType pathNodeType) {
      PathNode pathNode = null;
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      double d = getFeetY(this.cachedWorld, mutable.set(x, y, z));
      if (d - prevFeetY > 1.125D) {
         return null;
      } else {
         PathNodeType pathNodeType2 = this.getNodeType(this.entity, x, y, z);
         float f = this.entity.getPathfindingPenalty(pathNodeType2);
         double e = (double)this.entity.getWidth() / 2.0D;
         if (f >= 0.0F) {
            pathNode = this.getNode(x, y, z);
            pathNode.type = pathNodeType2;
            pathNode.penalty = Math.max(pathNode.penalty, f);
         }

         if (pathNodeType == PathNodeTypes.FENCE && pathNode != null && pathNode.penalty >= 0.0F && !this.method_29578(pathNode)) {
            pathNode = null;
         }

         if (pathNodeType2 != PathNodeTypes.WALKABLE) {
            if ((pathNode == null || pathNode.penalty < 0.0F) && maxYStep > 0 && pathNodeType2 != PathNodeTypes.FENCE && pathNodeType2 != PathNodeTypes.UNPASSABLE_RAIL && pathNodeType2 != PathNodeTypes.TRAPDOOR) {
               pathNode = this.getPathNode(x, y + 1, z, maxYStep - 1, prevFeetY, direction, pathNodeType);
               if (pathNode != null && (pathNode.type == PathNodeTypes.OPEN || pathNode.type == PathNodeTypes.WALKABLE) && this.entity.getWidth() < 1.0F) {
                  double g = (double) (x - direction.getOffsetX()) + 0.5D;
                  double h = (double) (z - direction.getOffsetZ()) + 0.5D;
                  Box box = new Box(g - e, getFeetY(this.cachedWorld, mutable.set(g, y + 1, h)) + 0.001D, h - e, g + e, (double) this.entity.getHeight() + getFeetY(this.cachedWorld, mutable.set(pathNode.x, pathNode.y, (double) pathNode.z)) - 0.002D, h + e);
                  if (this.method_29304(box)) {
                     pathNode = null;
                  }
               }
            }

            if (pathNodeType2 == PathNodeTypes.WATER && !this.canSwim()) {
               if (this.getNodeType(this.entity, x, y - 1, z) != PathNodeTypes.WATER) {
                  return pathNode;
               }

               while (y > 0) {
                  --y;
                  pathNodeType2 = this.getNodeType(this.entity, x, y, z);
                  if (pathNodeType2 != PathNodeTypes.WATER) {
                     return pathNode;
                  }

                  pathNode = this.getNode(x, y, z);
                  pathNode.type = pathNodeType2;
                  pathNode.penalty = Math.max(pathNode.penalty, this.entity.getPathfindingPenalty(pathNodeType2));
               }
            }

            if (pathNodeType2 == PathNodeTypes.OPEN) {
               int i = 0;
               int j = y;

               while (pathNodeType2 == PathNodeTypes.OPEN) {
                  --y;
                  PathNode pathNode4;
                  if (y < 0) {
                     pathNode4 = this.getNode(x, j, z);
                     pathNode4.type = PathNodeTypes.BLOCKED0;
                     pathNode4.penalty = -1.0F;
                     return pathNode4;
                  }

                  if (i++ >= this.entity.getSafeFallDistance()) {
                     pathNode4 = this.getNode(x, y, z);
                     pathNode4.type = PathNodeTypes.BLOCKED1;
                     pathNode4.penalty = -1.0F;
                     return pathNode4;
                  }

                  pathNodeType2 = this.getNodeType(this.entity, x, y, z);
                  f = this.entity.getPathfindingPenalty(pathNodeType2);
                  if (pathNodeType2 != PathNodeTypes.OPEN && f >= 0.0F) {
                     pathNode = this.getNode(x, y, z);
                     pathNode.type = pathNodeType2;
                     pathNode.penalty = Math.max(pathNode.penalty, f);
                     break;
                  }

                  if (f < 0.0F) {
                     pathNode4 = this.getNode(x, y, z);
                     pathNode4.type = PathNodeTypes.BLOCKED2;
                     pathNode4.penalty = -1.0F;
                     return pathNode4;
                  }
               }
            }

            if (pathNodeType2 == PathNodeTypes.FENCE) {
               pathNode = this.getNode(x, y, z);
               pathNode.visited = true;
               pathNode.type = pathNodeType2;
               pathNode.penalty = pathNodeType2.getDefaultPenalty();
            }
         }

         return pathNode;
      }
   }

   private boolean method_29304(Box box) {
      return this.field_25191.computeIfAbsent(box, (box2) -> !this.cachedWorld.isSpaceEmpty((Entity) this.entity, box));
   }

   public PathNodeType getNodeType(BlockView world, int x, int y, int z, PathfindingEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors) {
      PathNodeType pathNodeType = PathNodeTypes.BLOCKED3;
      BlockPos blockPos = mob.getBlockPos();
      Set<PathNodeType> nodeTypeSet = findNearbyNodeTypes(world, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, pathNodeType, blockPos);
      if (nodeTypeSet.contains(PathNodeTypes.FENCE)) {
         return PathNodeTypes.FENCE;
      } else if (nodeTypeSet.contains(PathNodeTypes.UNPASSABLE_RAIL)) {
         return PathNodeTypes.UNPASSABLE_RAIL;
      } else {
         PathNodeType pathNodeType2 = PathNodeTypes.BLOCKED4;

         for (PathNodeType pathNodeType3 : nodeTypeSet) {
            if (mob.getPathfindingPenalty(pathNodeType3) < 0.0F) {
               return pathNodeType3;
            }

            if (mob.getPathfindingPenalty(pathNodeType3) >= mob.getPathfindingPenalty(pathNodeType2)) {
               pathNodeType2 = pathNodeType3;
            }
         }

         return pathNodeType2;
      }
   }

   /**
    * Adds the node types in the box with the given size to the input EnumSet.
    * @return The node type at the least coordinates of the input box.
    */
   public Set<PathNodeType> findNearbyNodeTypes(BlockView world, int x, int y, int z, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors, PathNodeType type, BlockPos pos) {
      Set<PathNodeType> nearbyTypes = new HashSet<>();

      for(int i = 0; i < sizeX; ++i) {
         for(int j = 0; j < sizeY; ++j) {
            for(int k = 0; k < sizeZ; ++k) {
               int l = i + x;
               int m = j + y;
               int n = k + z;
               PathNodeType pathNodeType = this.getDefaultNodeType(world, l, m, n);
               pathNodeType = this.adjustNodeType(world, canOpenDoors, canEnterOpenDoors, pos, pathNodeType);
               if (i == 0 && j == 0 && k == 0) {
                  type = pathNodeType;
               }

               nearbyTypes.add(pathNodeType);
            }
         }
      }

      return nearbyTypes;
   }

   protected PathNodeType adjustNodeType(BlockView world, boolean canOpenDoors, boolean canEnterOpenDoors, BlockPos pos, PathNodeType type) {
      if (type == PathNodeTypes.DOOR_WOOD_CLOSED && canOpenDoors && canEnterOpenDoors) {
         type = PathNodeTypes.WALKABLE_DOOR;
      }

      if (type == PathNodeTypes.DOOR_OPEN && !canEnterOpenDoors) {
         type = PathNodeTypes.BLOCKED5;
      }

      if (type == PathNodeTypes.RAIL && !(world.getBlockState(pos).getBlock() instanceof AbstractRailBlock) && !(world.getBlockState(pos.down()).getBlock() instanceof AbstractRailBlock)) {
         type = PathNodeTypes.UNPASSABLE_RAIL;
      }

      if (type == PathNodeTypes.LEAVES) {
         type = PathNodeTypes.BLOCKED6;
      }

      return type;
   }

   private PathNodeType getNodeType(PathfindingEntity entity, BlockPos pos) {
      return this.getNodeType(entity, pos.getX(), pos.getY(), pos.getZ());
   }

   private PathNodeType getNodeType(PathfindingEntity PathfindingEntity, int i, int j, int k) {
      return this.cachedNodeTypes.computeIfAbsent(BlockPos.asLong(i, j, k), (l) -> {
         return this.getNodeType(this.cachedWorld, i, j, k, PathfindingEntity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors());
      });
   }

   public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
      return getLandNodeType(world, new BlockPos.Mutable(x, y, z));
   }

   public PathNodeType getLandNodeType(BlockView blockView, BlockPos.Mutable mutable) {
      int i = mutable.getX();
      int j = mutable.getY();
      int k = mutable.getZ();
      PathNodeType pathNodeType = getCommonNodeType(blockView, mutable);
      if (pathNodeType == PathNodeTypes.OPEN && j >= 1) {
         PathNodeType pathNodeType2 = getCommonNodeType(blockView, mutable.set(i, j - 1, k));
         pathNodeType = pathNodeType2 != PathNodeTypes.WALKABLE && pathNodeType2 != PathNodeTypes.OPEN && pathNodeType2 != PathNodeTypes.WATER && pathNodeType2 != PathNodeTypes.LAVA ? PathNodeTypes.WALKABLE : PathNodeTypes.OPEN;
         if (pathNodeType2 == PathNodeTypes.DAMAGE_FIRE) {
            pathNodeType = PathNodeTypes.DAMAGE_FIRE;
         }

         if (pathNodeType2 == PathNodeTypes.DAMAGE_CACTUS) {
            pathNodeType = PathNodeTypes.DAMAGE_CACTUS;
         }

         if (pathNodeType2 == PathNodeTypes.DAMAGE_OTHER) {
            pathNodeType = PathNodeTypes.DAMAGE_OTHER;
         }

         if (pathNodeType2 == PathNodeTypes.HONEY) {
            pathNodeType = PathNodeTypes.HONEY;
         }
      }

      if (pathNodeType == PathNodeTypes.WALKABLE) {
         pathNodeType = getNodeTypeFromNeighbors(blockView, mutable.set(i, j, k), pathNodeType);
      }

      return pathNodeType;
   }

   public PathNodeType getNodeTypeFromNeighbors(BlockView blockView, BlockPos.Mutable mutable, PathNodeType pathNodeType) {
      int i = mutable.getX();
      int j = mutable.getY();
      int k = mutable.getZ();

      for(int l = -1; l <= 1; ++l) {
         for(int m = -1; m <= 1; ++m) {
            for(int n = -1; n <= 1; ++n) {
               if (l != 0 || n != 0) {
                  mutable.set(i + l, j + m, k + n);
                  BlockState blockState = blockView.getBlockState(mutable);
                  if (blockState.isOf(Blocks.CACTUS)) {
                     return PathNodeTypes.DANGER_CACTUS;
                  }

                  if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
                     return PathNodeTypes.DANGER_OTHER;
                  }

                  if (isHot(blockState)) {
                     return PathNodeTypes.DANGER_FIRE;
                  }

                  if (blockView.getFluidState(mutable).isIn(FluidTags.WATER)) {
                     return PathNodeTypes.WATER_BORDER;
                  }
               }
            }
         }
      }

      return pathNodeType;
   }

   protected PathNodeType getCommonNodeType(BlockView blockView, BlockPos blockPos) {
      BlockState blockState = blockView.getBlockState(blockPos);
      Block block = blockState.getBlock();
      Material material = blockState.getMaterial();
      if (blockState.isAir()) {
         BlockState state2 = blockView.getBlockState(blockPos.offset(Direction.NORTH));
         if (isClimbableSolid(state2)) {
            return PathNodeTypes.DIRECTIONAL_NORTH;
         }

         state2 = blockView.getBlockState(blockPos.offset(Direction.EAST));
         if (isClimbableSolid(state2)) {
            return PathNodeTypes.DIRECTIONAL_EAST;
         }

         state2 = blockView.getBlockState(blockPos.offset(Direction.SOUTH));
         if (isClimbableSolid(state2)) {
            return PathNodeTypes.DIRECTIONAL_SOUTH;
         }

         state2 = blockView.getBlockState(blockPos.offset(Direction.WEST));
         if (isClimbableSolid(state2)) {
            return PathNodeTypes.DIRECTIONAL_WEST;
         }


         return PathNodeTypes.OPEN;
      } else if (!blockState.isIn(BlockTags.TRAPDOORS) && !blockState.isOf(Blocks.LILY_PAD)) {
         if (blockState.isOf(Blocks.CACTUS)) {
            return PathNodeTypes.DAMAGE_CACTUS;
         } else if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
            return PathNodeTypes.DAMAGE_OTHER;
         } else if (blockState.isOf(Blocks.HONEY_BLOCK)) {
            return PathNodeTypes.HONEY;
         } else if (blockState.isOf(Blocks.COCOA)) {
            return PathNodeTypes.COCOA;
         } else {
            FluidState fluidState = blockView.getFluidState(blockPos);
            if (fluidState.isIn(FluidTags.WATER)) {
               return PathNodeTypes.WATER;
            } else if (fluidState.isIn(FluidTags.LAVA)) {
               return PathNodeTypes.LAVA;
            } else if (isHot(blockState)) {
               return PathNodeTypes.DAMAGE_FIRE;
            } else if (DoorBlock.isWoodenDoor(blockState) && !(Boolean)blockState.get(DoorBlock.OPEN)) {
               return PathNodeTypes.DOOR_WOOD_CLOSED;
            } else if (block instanceof DoorBlock && material == Material.METAL && !(Boolean)blockState.get(DoorBlock.OPEN)) {
               return PathNodeTypes.DOOR_IRON_CLOSED;
            } else if (block instanceof DoorBlock && blockState.get(DoorBlock.OPEN)) {
               return PathNodeTypes.DOOR_OPEN;
            } else if (block instanceof AbstractRailBlock) {
               return PathNodeTypes.RAIL;
            } else if (block instanceof LeavesBlock) {
               return PathNodeTypes.LEAVES;
            } else if (!block.isIn(BlockTags.FENCES) && !block.isIn(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockState.get(FenceGateBlock.OPEN))) {
               return !blockState.canPathfindThrough(blockView, blockPos, NavigationType.LAND) ? PathNodeTypes.BLOCKED7 : PathNodeTypes.OPEN;
            } else {
               return PathNodeTypes.FENCE;
            }
         }
      } else {
         return PathNodeTypes.TRAPDOOR;
      }
   }

   private boolean isClimbableSolid(BlockState state) {
      return ClimbingSpeedRegistry.canClimb((LivingEntity) this.entity, state.getBlock()) && !state.getBlock().isIn(Dead.LADDER_LIKE);
   }

   private static boolean isHot(BlockState blockState) {
      return blockState.isIn(BlockTags.FIRE) || blockState.isOf(Blocks.LAVA) || blockState.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(blockState);
   }
}
