package org.cyclops.commoncapabilities.api.capability.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * An naive {@link ISlotlessItemHandler} wrapper around an {@link IItemHandler}.
 * This will perform a LIFO item algorithm.
 * @author rubensworks
 */
public class DefaultSlotlessItemHandlerWrapper implements ISlotlessItemHandler {

    private final IItemHandler itemHandler;

    public DefaultSlotlessItemHandlerWrapper(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public Iterator<ItemStack> getItems() {
        return new ItemHandlerItemStackIterator(itemHandler);
    }

    @Override
    public Iterator<ItemStack> findItems(@Nonnull ItemStack stack, int matchFlags) {
        return new FilteredItemHandlerItemStackIterator(itemHandler, stack, matchFlags);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            stack = itemHandler.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int amount, boolean simulate) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler.extractItem(i, amount, simulate);
            if (!itemStack.isEmpty()) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(@Nonnull ItemStack matchStack, int matchFlags, boolean simulate) {
        boolean compareStackSize = (matchFlags & ItemMatch.STACKSIZE) > 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            int amount = compareStackSize ? matchStack.getCount() : matchStack.getMaxStackSize();
            ItemStack tempItemStack;
            if (simulate || (!(tempItemStack = itemHandler.extractItem(i, amount, true)).isEmpty()
                    && ItemMatch.areItemStacksEqual(matchStack, tempItemStack, matchFlags))) {
                ItemStack itemStack = itemHandler.extractItem(i, amount, simulate);
                if (!itemStack.isEmpty() && ItemMatch.areItemStacksEqual(matchStack, itemStack, matchFlags)) {
                    return itemStack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
