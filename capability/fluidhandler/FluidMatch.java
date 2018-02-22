package org.cyclops.commoncapabilities.api.capability.fluidhandler;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Fluid matching flags.
 * @author rubensworks
 */
public final class FluidMatch {

    /**
     * Convenience value matching FluidStacks only by Fluid.
     */
    public static final int ANY = 0;
    /**
     * Match FluidStacks NBT tags.
     */
    public static final int NBT = 1;
    /**
     * Match FluidStacks amounts.
     */
    public static final int AMOUNT = 2;
    /**
     * Convenience value matching FluidStacks exactly by NBT tag and amount.
     */
    public static final int EXACT = NBT | AMOUNT;

    public static boolean areFluidStacksEqual(@Nullable FluidStack a, @Nullable FluidStack b, int matchFlags) {
        boolean nbt    = (matchFlags & NBT   ) > 0;
        boolean amount = (matchFlags & AMOUNT) > 0;
        return a == b ||
                (a != null && b != null
                        && a.getFluid() == b.getFluid()
                        && (!amount || a.amount == b.amount)
                        && (!nbt || FluidStack.areFluidStackTagsEqual(a, b)));
    }

}
