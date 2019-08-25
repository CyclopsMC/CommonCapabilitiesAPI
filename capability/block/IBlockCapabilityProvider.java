package org.cyclops.commoncapabilities.api.capability.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Equivalent of {@link ICapabilityProvider} for blocks that do not have an internal state.
 * Register a provider at {@link BlockCapabilities}.
 * @author rubensworks
 */
public interface IBlockCapabilityProvider {

    /**
     * Retrieves the handler for the capability of the given block requested at the given position.
     * The position is identified by the World, BlockPos and EnumFacing.
     * The return value CAN be null if the block does not support the capability.
     * The return value CAN be the same for multiple faces.
     *
     * @param blockState The blockstate to retrieve the capability from
     * @param capability The capability to check
     * @param world The world in which the given block exists
     * @param pos The position at which the given block exists
     * @param facing The Side to check from:
     *   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @param <T> The capability type.
     * @return The requested capability.
     */
    <T> LazyOptional<T> getCapability(@Nonnull BlockState blockState, @Nonnull Capability<T> capability,
                                      @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction facing);

}
