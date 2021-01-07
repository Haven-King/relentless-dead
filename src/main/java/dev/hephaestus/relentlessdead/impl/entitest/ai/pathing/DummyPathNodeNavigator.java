package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DummyPathNodeNavigator extends PathNodeNavigator {
	public DummyPathNodeNavigator() {
		super(null, 0);
	}

	@Override
	public @Nullable Path findPathToAny(ChunkCache world, MobEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
		return null;
	}
}
