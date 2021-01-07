package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

import dev.hephaestus.relentlessdead.impl.Dead;
import net.minecraft.util.math.Direction;

public class PathNodeTypes {
	public static final PathNodeType BLOCKED0 = new PathNodeType(Dead.id("path", "blocked", "0"), -1.0F);
	public static final PathNodeType BLOCKED1 = new PathNodeType(Dead.id("path", "blocked", "1"), -1.0F);
	public static final PathNodeType BLOCKED2 = new PathNodeType(Dead.id("path", "blocked", "2"), -1.0F);
	public static final PathNodeType BLOCKED3 = new PathNodeType(Dead.id("path", "blocked", "3"), -1.0F);
	public static final PathNodeType BLOCKED4 = new PathNodeType(Dead.id("path", "blocked", "4"), -1.0F);
	public static final PathNodeType BLOCKED5 = new PathNodeType(Dead.id("path", "blocked", "5"), -1.0F);
	public static final PathNodeType BLOCKED6 = new PathNodeType(Dead.id("path", "blocked", "6"), -1.0F);
	public static final PathNodeType BLOCKED7 = new PathNodeType(Dead.id("path", "blocked", "7"), -1.0F);

	public static final PathNodeType OPEN = new PathNodeType(Dead.id("path", "open"), 0.1F);
	public static final PathNodeType WALKABLE = new PathNodeType(Dead.id("path", "walkable"), 0.1F);
	public static final PathNodeType WALKABLE_DOOR = new PathNodeType(Dead.id("path", "door", "walkable"), 0.1F);
	public static final PathNodeType TRAPDOOR = new PathNodeType(Dead.id("path", "trapdoor"), 0.1F);
	public static final PathNodeType FENCE = new PathNodeType(Dead.id("path", "fence"), -1.0F);
	public static final PathNodeType LAVA = new PathNodeType(Dead.id("path", "lava"), -1.0F);
	public static final PathNodeType WATER = new PathNodeType(Dead.id("path", "water"), 8.0F);
	public static final PathNodeType WATER_BORDER = new PathNodeType(Dead.id("path", "water_border"), 8.0F);
	public static final PathNodeType RAIL = new PathNodeType(Dead.id("path", "rail"), 0.1F);
	public static final PathNodeType UNPASSABLE_RAIL = new PathNodeType(Dead.id("path", "unpassable_rail"), -1.0F);
	public static final PathNodeType DANGER_FIRE = new PathNodeType(Dead.id("path", "danger", "fire"), 8.0F);
	public static final PathNodeType DAMAGE_FIRE = new PathNodeType(Dead.id("path", "damage", "fire"), 16.0F);
	public static final PathNodeType DANGER_CACTUS = new PathNodeType(Dead.id("path", "danger", "cactus"), 8.0F);
	public static final PathNodeType DAMAGE_CACTUS = new PathNodeType(Dead.id("path", "damage", "cactus"), -1.0F);
	public static final PathNodeType DANGER_OTHER = new PathNodeType(Dead.id("path", "danger", "other"), 8.0F);
	public static final PathNodeType DAMAGE_OTHER = new PathNodeType(Dead.id("path", "damage", "other"), -1.0F);
	public static final PathNodeType DOOR_OPEN = new PathNodeType(Dead.id("path", "door", "open"), 0.1F);
	public static final PathNodeType DOOR_WOOD_CLOSED = new PathNodeType(Dead.id("path", "door", "wooden", "closed"), -1.0F);
	public static final PathNodeType DOOR_IRON_CLOSED = new PathNodeType(Dead.id("path", "door", "iron", "closed"), -1.0F);
	public static final PathNodeType BREACH = new PathNodeType(Dead.id("path", "breach"), 4.0F);
	public static final PathNodeType LEAVES = new PathNodeType(Dead.id("path", "leaves"), -1.0F);
	public static final PathNodeType HONEY = new PathNodeType(Dead.id("path", "honey"), 8.0F);
	public static final PathNodeType.Directional DIRECTIONAL_NORTH = new PathNodeType.Directional(Dead.id("path", "climbable", "north"), 0.2F, Direction.NORTH);
	public static final PathNodeType.Directional DIRECTIONAL_EAST = new PathNodeType.Directional(Dead.id("path", "climbable", "south"), 0.2F, Direction.SOUTH);
	public static final PathNodeType.Directional DIRECTIONAL_SOUTH = new PathNodeType.Directional(Dead.id("path", "climbable", "east"), 0.2F, Direction.EAST);
	public static final PathNodeType.Directional DIRECTIONAL_WEST = new PathNodeType.Directional(Dead.id("path", "climbable", "west"), 0.2F, Direction.WEST);
	public static final PathNodeType.Directional.Terminator TERMINATOR_NORTH = new PathNodeType.Directional.Terminator(Dead.id("path", "terminator", "north"), 0.2F, Direction.NORTH);
	public static final PathNodeType.Directional.Terminator TERMINATOR_EAST = new PathNodeType.Directional.Terminator(Dead.id("path", "terminator", "south"), 0.2F, Direction.SOUTH);
	public static final PathNodeType.Directional.Terminator TERMINATOR_SOUTH = new PathNodeType.Directional.Terminator(Dead.id("path", "terminator", "east"), 0.2F, Direction.EAST);
	public static final PathNodeType.Directional.Terminator TERMINATOR_WEST = new PathNodeType.Directional.Terminator(Dead.id("path", "terminator", "west"), 0.2F, Direction.WEST);
	public static final PathNodeType COCOA = new PathNodeType(Dead.id("path", "cocoa"), 0.1F);
}
