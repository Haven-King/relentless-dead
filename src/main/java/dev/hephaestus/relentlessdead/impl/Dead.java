package dev.hephaestus.relentlessdead.impl;

import dev.hephaestus.climbable.api.ClimbingSpeedRegistry;
import dev.hephaestus.relentlessdead.api.AngerableEntity;
import dev.hephaestus.relentlessdead.api.RelentlessDead;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class Dead implements ModInitializer {
	public static final String MOD_ID = "relentless-dead";
	private static final HashMap<Identifier, Identifier> IDS = new HashMap<>();
	public static final Tag<Block> LADDER_LIKE = TagRegistry.block(id("ladder_like"));

	@Override
	public void onInitialize() {
		ClimbingSpeedRegistry.registerClimbableTag(RelentlessDead.ZOMBIE_CLIMBABLE, Dead::climbingSpeed, e -> e.getType().isIn(RelentlessDead.ZOMBIES) && !e.isBaby());
	}

	public static Identifier id(String... path) {
		return IDS.computeIfAbsent(new Identifier(MOD_ID, String.join(".", path)), id -> id);
	}

	private static double climbingSpeed(LivingEntity e) {
		double speed = 0.75;

		if (e instanceof AngerableEntity && Config.ZOMBIES_GET_ANGRY) {
			speed += (float) ((AngerableEntity) e).getAnger() / ((AngerableEntity) e).maxAnger() * 2;
		}

		return speed;
	}
}
