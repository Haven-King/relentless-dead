package dev.hephaestus.relentlessdead.impl.entitest;

import dev.hephaestus.relentlessdead.impl.entitest.ai.pathing.EntityNavigation;
import dev.hephaestus.relentlessdead.impl.entitest.ai.pathing.PathNodeType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.mob.MobVisibilityCache;
import net.minecraft.fluid.Fluid;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Random;

public interface PathfindingEntity {
	void setPathfindingPenalty(PathNodeType nodeType, float weight);
	void setTarget(LivingEntity entity);
	void setAttacking(boolean attacking);
	void swingHand(Hand hand);

	double getAttributeValue(EntityAttribute attribute);
	double squaredDistanceTo(Entity entity);
	double getStepHeight();
	double getEyeY();
	double getX();
	double getY();
	double getZ();

	float getPathfindingPenalty(PathNodeType nodeType);
	float getWidth();
	float getHeight();
	float getMovementSpeed();

	boolean canWalkOnFluid(Fluid fluid);
	boolean isInWalkTargetRange(BlockPos pos);
	boolean isInsideWaterOrBubbleColumn();
	boolean isInLava();
	boolean isTouchingWater();
	boolean method_29244(PathNodeType nodeType);
	boolean isOnGround();
	boolean hasVehicle();
	boolean tryAttack(Entity target);

	BlockPos getBlockPos();

	Box getBoundingBox();

	int getSafeFallDistance();

	LivingEntity getTarget();

	AbstractTeam getScoreboardTeam();

	MobVisibilityCache getVisibilityCache();

	Random getRandom();

	LookControl getLookControl();

	MoveControl getMoveControl();

	EntityNavigation getZombieNavigation();

	World getWorld();

	JumpControl getJumpControl();
}
