package org.cyclops.commoncapabilities.api.capability.wrench;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

/**
 * An indicator for what is being targeted by a wrench.
 * @author rubensworks
 */
public class WrenchTarget {
    private final HitResult.Type type;
    private final Level world;
    private final BlockPos pos;
    private final Direction side;
    private final Entity entity;

    protected WrenchTarget(HitResult.Type type, Level world, BlockPos pos, Direction side, Entity entity) {
        this.type = type;
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.entity = entity;
    }

    public static WrenchTarget forBlock(Level world, BlockPos pos, Direction side) {
        return new WrenchTarget(HitResult.Type.BLOCK, world, pos, side, null);
    }

    public static WrenchTarget forEntity(Entity entity) {
        return new WrenchTarget(HitResult.Type.ENTITY, null, null, null, entity);
    }

    public static WrenchTarget forNone() {
        return new WrenchTarget(HitResult.Type.MISS, null, null, null, null);
    }

    public HitResult.Type getType() {
        return type;
    }

    public @Nullable Level getLevel() {
        return world;
    }

    public @Nullable BlockPos getPos() {
        return pos;
    }

    public @Nullable
    Direction getSide() {
        return side;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }
}
