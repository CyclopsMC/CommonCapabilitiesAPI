package org.cyclops.commoncapabilities.api.capability.fluidhandler;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over all slots in a fluid handler.
 * @author rubensworks
 */
public class FluidHandlerFluidStackIterator implements Iterator<FluidStack> {

    private final IFluidHandler fluidHandler;
    private int slot;

    public FluidHandlerFluidStackIterator(IFluidHandler fluidHandler, int offset) {
        this.fluidHandler = fluidHandler;
        this.slot = offset;
    }

    public FluidHandlerFluidStackIterator(IFluidHandler fluidHandler) {
        this(fluidHandler, 0);
    }

    @Override
    public boolean hasNext() {
        return slot < fluidHandler.getTanks();
    }

    @Override
    public FluidStack next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Slot out of bounds");
        }
        return fluidHandler.getFluidInTank(slot++);
    }
}
