package org.cyclops.commoncapabilities.api.ingredient;

import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * A tank id inside a fluid handler.
 * @author rubensworks
 */
public class FluidHandlerRecipeTarget {

    private final IFluidHandler fluidHandler;
    private final int slot;

    public FluidHandlerRecipeTarget(IFluidHandler fluidHandler, int slot) {
        this.fluidHandler = fluidHandler;
        this.slot = slot;
    }

    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return "[FluidHandlerRecipeTarget slot " + slot + "]";
    }
}
