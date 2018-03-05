package org.cyclops.commoncapabilities.api.capability.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over all slots in an item handler.
 * @author rubensworks
 */
public class ItemHandlerItemStackIterator implements Iterator<ItemStack> {

    private final IItemHandler itemHandler;
    private int slot = 0;

    public ItemHandlerItemStackIterator(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public boolean hasNext() {
        return slot < itemHandler.getSlots();
    }

    @Override
    public ItemStack next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Slot out of bounds");
        }
        return itemHandler.getStackInSlot(slot++);
    }
}
