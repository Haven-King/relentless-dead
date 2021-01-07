package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DummyNavigation extends EntityNavigation {
	public DummyNavigation(MobEntity mob, World world) {
		super(mob, world);
	}

	@Override
	protected PathNodeNavigator createPathNodeNavigator(int range) {
		return new DummyPathNodeNavigator();
	}

	@Override
	public void tick() {

	}



	@Override
	protected Vec3d getPos() {
		return this.entity.getPos();
	}

	@Override
	protected boolean isAtValidPosition() {
		return false;
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
		return false;
	}
}
