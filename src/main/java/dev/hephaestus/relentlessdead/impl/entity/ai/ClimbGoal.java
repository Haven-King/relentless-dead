package dev.hephaestus.relentlessdead.impl.entity.ai;

import dev.hephaestus.relentlessdead.api.RelentlessDead;
import dev.hephaestus.relentlessdead.impl.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ClimbGoal extends Goal {
	private final MobEntity mobEntity;

	public ClimbGoal(MobEntity mobEntity) {
		this.mobEntity = mobEntity;
	}

	@Override
	public boolean canStart() {
		if (!Config.ZOMBIES_CAN_CLIMB || this.mobEntity.getTarget() == null || this.mobEntity.getY() >= this.mobEntity.getTarget().getY() + 1D) {
			return false;
		}

		BlockPos.Mutable pos = new BlockPos.Mutable(this.mobEntity.getX(), this.mobEntity.getEyeY(), this.mobEntity.getZ());
		pos.move(Direction.fromRotation(this.mobEntity.getHeadYaw()));

		for (int i = 0; i < Math.ceil(this.mobEntity.getHeight()) + 1; ++i, pos.move(Direction.DOWN, 1)) {
			Block block = this.mobEntity.world.getBlockState(pos).getBlock();

			if (block.isIn(RelentlessDead.ZOMBIE_CLIMBABLE)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinue() {
		return this.mobEntity.getTarget() != null && this.mobEntity.getY() + 1D <= this.mobEntity.getTarget().getY();
	}

	@Override
	public void tick() {
		Vec3d pos = this.mobEntity.getTarget().getPos();
		this.mobEntity.getMoveControl().moveTo(Math.floor(pos.x) + 0.5, pos.y, Math.floor(pos.z + 0.5), 1D);
//		this.mobEntity.getJumpControl().setActive();
	}
}
