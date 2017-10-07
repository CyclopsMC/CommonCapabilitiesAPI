package org.cyclops.commoncapabilities.api.capability.recipehandler;

import net.minecraftforge.items.IItemHandler;

/**
 * A slot inside an item handler.
 * @author rubensworks
 */
public class ItemHandlerRecipeTarget {

    private final IItemHandler itemHandler;
    private final int slot;

    public ItemHandlerRecipeTarget(IItemHandler itemHandler, int slot) {
        this.itemHandler = itemHandler;
        this.slot = slot;
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return "[ItemHandlerRecipeTarget slot " + slot + "]";
    }
}
