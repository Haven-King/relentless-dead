package dev.hephaestus.relentlessdead.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class ChangeVisibilityType {
	@Redirect(method = "canSee", at = @At(value = "NEW", target = "net/minecraft/world/RaycastContext"))
	private RaycastContext makeVisual(Vec3d start, Vec3d end, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, Entity entity) {
		return new RaycastContext(start, end, RaycastContext.ShapeType.VISUAL, fluidHandling, entity);
	}
}
