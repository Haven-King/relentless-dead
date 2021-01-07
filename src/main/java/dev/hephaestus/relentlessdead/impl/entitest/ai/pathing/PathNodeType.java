package dev.hephaestus.relentlessdead.impl.entitest.ai.pathing;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class PathNodeType {

   private final Identifier id;
   private final float defaultPenalty;

   PathNodeType(Identifier id, float defaultPenalty) {
      this.id = id;
      this.defaultPenalty = defaultPenalty;
   }

   public float getDefaultPenalty() {
      return this.defaultPenalty;
   }

   public static class Directional extends PathNodeType {
      private final Direction direction;

      Directional(Identifier id, float defaultPenalty, Direction direction) {
         super(id, defaultPenalty);
         this.direction = direction;
      }

      public Direction getDirection() {
         return this.direction;
      }

      public static Directional of(Direction direction) {
         switch (direction) {
            case NORTH:
               return PathNodeTypes.DIRECTIONAL_NORTH;
            case EAST:
               return PathNodeTypes.DIRECTIONAL_EAST;
            case SOUTH:
               return PathNodeTypes.DIRECTIONAL_SOUTH;
            case WEST:
               return PathNodeTypes.DIRECTIONAL_WEST;
            default:
               return null;
         }
      }

      public static class Terminator extends Directional {
         Terminator(Identifier id, float defaultPenalty, Direction direction) {
            super(id, defaultPenalty, direction);
         }

         public static Directional of(Direction direction) {
            switch (direction) {
               case NORTH:
                  return PathNodeTypes.TERMINATOR_NORTH;
               case EAST:
                  return PathNodeTypes.TERMINATOR_EAST;
               case SOUTH:
                  return PathNodeTypes.TERMINATOR_SOUTH;
               case WEST:
                  return PathNodeTypes.TERMINATOR_WEST;
               default:
                  return null;
            }
         }
      }
   }
}
