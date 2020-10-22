package dev.hephaestus.relentlessdead.mixin;

import dev.hephaestus.relentlessdead.api.AngerableEntity;
import dev.hephaestus.relentlessdead.api.RelentlessDead;
import dev.hephaestus.relentlessdead.impl.Config;
import dev.hephaestus.relentlessdead.impl.entity.ai.BreakObstructionGoal;
import dev.hephaestus.relentlessdead.impl.entity.ai.ClimbGoal;
import dev.hephaestus.relentlessdead.impl.entity.ai.pathing.ZombieNavigation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
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
public abstract class ModifyZombie extends HostileEntity implements AngerableEntity {
	@Unique
	private int anger = 0;

	protected ModifyZombie(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
	private void changeNavigation(EntityType<? extends ZombieEntity> entityType, World world, CallbackInfo ci) {
		this.navigation = new ZombieNavigation(this, world);
	}

	@Inject(method = "initGoals", at = @At("TAIL"))
	private void addGoals(CallbackInfo ci) {
		this.goalSelector.add(4, new BreakObstructionGoal(this));
		this.goalSelector.add(1, new ClimbGoal(this));
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
			instance.removeModifier(RelentlessDead.ANGER_SPEED_ID);
		}
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
}
