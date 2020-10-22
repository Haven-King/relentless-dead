package dev.hephaestus.relentlessdead.api;

import dev.hephaestus.relentlessdead.impl.Dead;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;

import java.util.UUID;

public class RelentlessDead {
	public static Tag<Block> ZOMBIE_BREAKABLE = TagRegistry.block(Dead.id("zombie_breakable"));
	public static Tag<Block> ZOMBIE_CLIMBABLE = TagRegistry.block(Dead.id("zombie_climbable"));
	public static Tag<EntityType<?>> ZOMBIES = TagRegistry.entityType(Dead.id("zombies"));

	public static final UUID ANGER_SPEED_ID = UUID.fromString("B97667A9-1337-4402-BC1F-2EE2A276D416");
}
