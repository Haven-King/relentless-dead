package dev.hephaestus.relentlessdead.impl.entity.ai;

import dev.hephaestus.relentlessdead.api.RelentlessDead;
import dev.hephaestus.relentlessdead.impl.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BreakObstructionGoal extends Goal {
	private final MobEntity mobEntity;
	private BlockPos breakTarget;
	private Vec3d breakCenter;
	private BlockState breakState;
	private float breakTimer;
	private float blockBreakingSoundCooldown;

	public BreakObstructionGoal(MobEntity mobEntity) {
		this.mobEntity = mobEntity;
	}

	@Override
	public boolean canStart() {
		return !this.mobEntity.isBaby() && Config.ZOMBIES_BREAK_BLOCKS && this.mobEntity.getTarget() != null && this.mobEntity.isOnGround() && this.isStill() && this.getBreakTarget() != null;
	}

	@Override
	public void start() {
		this.breakTarget = this.getBreakTarget();
		this.breakCenter = new Vec3d(this.breakTarget.getX() + 0.5, this.breakTarget.getY() + 0.5, this.breakTarget.getZ() + 0.5);
		this.breakState = this.mobEntity.world.getBlockState(this.breakTarget);
		this.breakTimer = 0F;
	}

	@Override
	public void tick() {
		float newBreakTimer = this.breakTimer + this.getBreakDelta();

		int i1 = (int) breakTimer;
		int i2 = (int) newBreakTimer;

		if (i1 < i2) {
			this.mobEntity.world.setBlockBreakingInfo(this.mobEntity.getEntityId(), this.breakTarget, i2);
		}

		this.breakTimer = newBreakTimer;

		this.mobEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, this.breakCenter);

		if (this.mobEntity.getRandom().nextInt(20) == 0) {
			this.mobEntity.world.syncWorldEvent(1019, this.breakTarget, 0);
			if (!this.mobEntity.handSwinging) {
				this.mobEntity.swingHand(this.mobEntity.getActiveHand());
			}
		}

		if (this.breakTimer >= 10) {
			this.mobEntity.world.breakBlock(this.breakTarget, true, this.mobEntity);
		} else if (this.blockBreakingSoundCooldown % 4F == 0F) {
			BlockSoundGroup blockSoundGroup = this.breakState.getSoundGroup();
			this.mobEntity.world.playSound(
					null,
					this.breakTarget,
					blockSoundGroup.getHitSound(),
					SoundCategory.BLOCKS,
					(blockSoundGroup.getVolume() + 1.0F) / 8.0F, blockSoundGroup.getPitch() * 0.5F
			);
		}

		++this.blockBreakingSoundCooldown;
	}

	@Override
	public void stop() {
		this.breakTarget = null;
		this.breakState = null;
	}

	@Override
	public boolean canStop() {
		return this.breakTimer < 0;
	}

	@Override
	public boolean shouldContinue() {
		return this.breakTimer > 0 && this.mobEntity.world.getBlockState(this.breakTarget).isIn(RelentlessDead.ZOMBIE_BREAKABLE);
	}

	private BlockPos getBreakTarget() {
		BlockPos.Mutable pos = new BlockPos.Mutable(this.mobEntity.getX(), this.mobEntity.getEyeY(), this.mobEntity.getZ());
		pos.move(Direction.fromRotation(this.mobEntity.getHeadYaw()));

		for (int i = 0; i < Math.ceil(this.mobEntity.getHeight()); ++i, pos.move(Direction.DOWN, 1)) {
			Block block = this.mobEntity.world.getBlockState(pos).getBlock();

			if (block.isIn(RelentlessDead.ZOMBIE_BREAKABLE)) {
				return pos;
			}
		}

		if (this.mobEntity.getTarget() != null) {
			BlockPos targetPos = this.mobEntity.getTarget().getBlockPos();
			BlockPos mob = this.mobEntity.getBlockPos();

			if (targetPos.getX() == mob.getX() && targetPos.getZ() == mob.getZ()) {
				BlockPos p;
				if (targetPos.getY() > mob.getY()) {
					p = mob.offset(Direction.UP, (int) Math.ceil(this.mobEntity.getHeight()));
				} else if (targetPos.getY() < mob.getY()) {
					p = mob.offset(Direction.DOWN);
				} else {
					return null;
				}

				if (this.mobEntity.world.getBlockState(p).isIn(RelentlessDead.ZOMBIE_BREAKABLE)) {
					return p;
				}
			}
		}

		return null;
	}

	private boolean isUsingEffectiveTool(BlockState block) {
		return !block.isToolRequired() || this.mobEntity.getMainHandStack().isEffectiveOn(block);
	}

	private boolean isStill() {
		double v = this.mobEntity.getVelocity().length();
		return v < 0.08;
	}

	private float getBreakDelta() {
		float f = this.breakState.getHardness(this.mobEntity.world, this.breakTarget);
		int i = this.isUsingEffectiveTool(this.breakState) ? 30 : 100;

		return this.getBlockBreakingSpeed(this.breakState) / f / (float)i * 10 * Config.ZOMBIE_BREAK_SPEED_MODIFIER;
	}

	private float getBlockBreakingSpeed(BlockState state) {
		float f = this.mobEntity.getMainHandStack().getMiningSpeedMultiplier(state);
		if (f > 1.0F) {
			int i = EnchantmentHelper.getEfficiency(this.mobEntity);
			ItemStack itemStack = this.mobEntity.getMainHandStack();
			if (i > 0 && !itemStack.isEmpty()) {
				f += (float)(i * i + 1);
			}
		}

		if (StatusEffectUtil.hasHaste(this.mobEntity)) {
			f *= 1.0F + (float)(StatusEffectUtil.getHasteAmplifier(this.mobEntity) + 1) * 0.2F;
		}

		if (this.mobEntity.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
			float k;
			switch(this.mobEntity.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
				case 0:
					k = 0.3F;
					break;
				case 1:
					k = 0.09F;
					break;
				case 2:
					k = 0.0027F;
					break;
				case 3:
				default:
					k = 8.1E-4F;
			}

			f *= k;
		}

		if (this.mobEntity.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this.mobEntity)) {
			f /= 5.0F;
		}

		if (!this.mobEntity.isOnGround()) {
			f /= 5.0F;
		}

		return f;
	}
}
