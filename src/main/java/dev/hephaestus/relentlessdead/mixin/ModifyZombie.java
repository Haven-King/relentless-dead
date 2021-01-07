package dev.hephaestus.relentlessdead.mixin;

import dev.hephaestus.relentlessdead.api.AngerableEntity;
import dev.hephaestus.relentlessdead.api.RelentlessDead;
import dev.hephaestus.relentlessdead.impl.Config;
import dev.hephaestus.relentlessdead.impl.entitest.PathfindingEntity;
import dev.hephaestus.relentlessdead.impl.entitest.ai.goal.FollowTargetGoal;
import dev.hephaestus.relentlessdead.impl.entitest.ai.goal.ZombieAttackGoal;
import dev.hephaestus.relentlessdead.impl.entitest.ai.pathing.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ModifyZombie extends HostileEntity implements AngerableEntity, PathfindingEntity {
	@Unique
	private int anger = 0;

	@Unique
	private MobNavigation zombieNavigation;

	protected ModifyZombie(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
	private void changeNavigation(EntityType<? extends ZombieEntity> entityType, World world, CallbackInfo ci) {
		this.navigation = new DummyNavigation(this, world);
		this.zombieNavigation = new MobNavigation(this, world);
	}

	@Inject(method = "initCustomGoals", at = @At("TAIL"))
	private void addGoals(CallbackInfo ci) {
//		this.goalSelector.add(4, new BreakObstructionGoal(this));
//		this.goalSelector.add(1, new ClimbGoal(this));
		this.goalSelector.add(2, new ZombieAttackGoal(this, 1D, false));
		this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal<>(this, MerchantEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
		this.targetSelector.add(5, new FollowTargetGoal<>(this, TurtleEntity.class, true));
	}

	@Inject(method = "damage", at = @At(value = "RETURN"))
	private void updateAnger(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && source.getAttacker() instanceof PlayerEntity && !source.isSourceCreativePlayer()) {
			this.anger += amount;
			if (Config.ZOMBIES_GET_ANGRY) {
				EntityAttributeInstance instance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
				instance.removeModifier(RelentlessDead.ANGER_SPEED_ID);
				instance.addPersistentModifier(new EntityAttributeModifier(RelentlessDead.ANGER_SPEED_ID, "Anger speed boost",
						(float) (this.getAnger() / this.maxAnger()) / 10
						, EntityAttributeModifier.Operation.MULTIPLY_BASE)
				);
			}
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void removeAngrySpeed(CallbackInfo ci) {
		if (!Config.ZOMBIES_GET_ANGRY) {
			EntityAttributeInstance instance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

			if (instance != null) {
				instance.removeModifier(RelentlessDead.ANGER_SPEED_ID);
			}
		}
	}

	@Inject(method = "tickMovement", at = @At("TAIL"))
	private void tickZombieNavigation(CallbackInfo ci) {
		this.world.getProfiler().push("relentless-dead:navigation");
		this.getZombieNavigation().tick();
		this.world.getProfiler().pop();
	}

	@Override
	public int getAnger() {
		return this.anger;
	}

	@Override
	public int maxAnger() {
		return (int) this.getMaxHealth();
	}

	@Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
	private void addAngerToTag(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("RelentlessDeadAnger", this.anger);
	}

	@Inject(method = "readCustomDataFromTag", at = @At("TAIL"))
	private void readAngerFromTag(CompoundTag tag, CallbackInfo ci) {
		this.anger = tag.getInt("RelentlessDeadAnger");
	}

	@Override
	public void setPathfindingPenalty(PathNodeType nodeType, float weight) {

	}

	@Override
	public float getPathfindingPenalty(PathNodeType nodeType) {
		return nodeType.getDefaultPenalty();
	}

	@Override
	public boolean method_29244(PathNodeType nodeType) {
		return nodeType != PathNodeTypes.DANGER_FIRE && nodeType != PathNodeTypes.DANGER_CACTUS && nodeType != PathNodeTypes.DANGER_OTHER && nodeType != PathNodeTypes.WALKABLE_DOOR;
	}

	@Override
	public EntityNavigation getZombieNavigation() {
		return this.zombieNavigation;
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public double getStepHeight() {
		return this.stepHeight;
	}
}
