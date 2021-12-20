package org.cyclops.commoncapabilities.api.capability.fluidhandler;

import net.minecraftforge.fluids.FluidStack;

/**
 * Fluid matching flags.
 * @author rubensworks
 */
public final class FluidMatch {

    /**
     * Convenience value matching any FluidStack.
     */
    public static final int ANY = 0;
    /**
     * Match FluidStack fluids.
     */
    public static final int FLUID = 1;
    /**
     * Match FluidStacks NBT tags.
     */
    public static final int TAG = 2;
    /**
     * Match FluidStacks amounts.
     */
    public static final int AMOUNT = 4;
    /**
     * Convenience value matching FluidStacks exactly by fluid, NBT tag and amount.
     */
    public static final int EXACT = FLUID | TAG | AMOUNT;

    public static boolean areFluidStacksEqual(FluidStack a, FluidStack b, int matchFlags) {
        if (matchFlags == ANY) {
            return true;
        }
        boolean fluid  = (matchFlags & FLUID ) > 0;
        boolean nbt    = (matchFlags & TAG) > 0;
        boolean amount = (matchFlags & AMOUNT) > 0;
        return a == b || a.isEmpty() && b.isEmpty() ||
                (!a.isEmpty() && !b.isEmpty()
                        && (!fluid || a.getFluid() == b.getFluid())
                        && (!amount || a.getAmount() == b.getAmount())
                        && (!nbt || FluidStack.areFluidStackTagsEqual(a, b)));
    }

}
