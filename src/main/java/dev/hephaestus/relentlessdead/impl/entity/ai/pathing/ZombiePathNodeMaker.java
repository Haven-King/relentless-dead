package dev.hephaestus.relentlessdead.impl.entity.ai.pathing;

import dev.hephaestus.relentlessdead.api.RelentlessDead;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class ZombiePathNodeMaker extends LandPathNodeMaker {
	@Override
	public PathNodeType getNodeType(BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors) {
		BlockPos.Mutable pos = new BlockPos.Mutable(x, y, z);
		BlockState state = world.getBlockState(pos);

		if (state.isIn(RelentlessDead.ZOMBIE_CLIMBABLE)) {
			return PathNodeType.OPEN;
		}

		if (state.isIn(RelentlessDead.ZOMBIE_BREAKABLE)) {
			return PathNodeType.OPEN;
		}



		return super.getNodeType(world, x, y, z, mob, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors);
	}
}
