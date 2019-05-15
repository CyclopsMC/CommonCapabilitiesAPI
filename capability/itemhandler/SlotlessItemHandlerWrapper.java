package org.cyclops.commoncapabilities.api.capability.itemhandler;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.PrimitiveIterator;

/**
 * An abstract {@link ISlotlessItemHandler} wrapper around an {@link IItemHandler}.
 * @author rubensworks
 */
public abstract class SlotlessItemHandlerWrapper implements ISlotlessItemHandler {

    protected final IItemHandler itemHandler;

    public SlotlessItemHandlerWrapper(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    /**
     * Get the slots in which the given ItemStack is present according to the given match flags.
     * Stacksize of the item in the slot must be below the maximum stack size,
     * so there must be room left in the slot.
     * @param itemStack The ItemStack to look for.
     * @param matchFlags The flags to match the given ItemStack by.
     * @return The slots in which the ItemStack are present.
     */
    protected abstract PrimitiveIterator.OfInt getNonFullSlotsWithItemStack(@Nonnull ItemStack itemStack, int matchFlags);

    /**
     * Get the slots in which the given ItemStack is present according to the given match flags.
     * Stacksize of the item in the slot must be larger than zero.
     * @param itemStack The ItemStack to look for.
     * @param matchFlags The flags to match the given ItemStack by.
     * @return The slots in which the ItemStack are present.
     */
    protected abstract PrimitiveIterator.OfInt getNonEmptySlotsWithItemStack(@Nonnull ItemStack itemStack, int matchFlags);

    /**
     * Get an iterator over all slots in which the given ItemStack is present according to the given match flags.
     * Stacksize of the item in the slot must be larger than zero.
     * @param itemStack The ItemStack to look for.
     * @param matchFlags The flags to match the given ItemStack by.
     * @return An iterator over all slots in which the ItemStack is present.
     */
    protected abstract PrimitiveIterator.OfInt getSlotsWithItemStack(@Nonnull ItemStack itemStack, int matchFlags);

    /**
     * @return The slots with no ItemStack.
     */
    protected abstract PrimitiveIterator.OfInt getEmptySlots();

    /**
     * @return The slots that are not empty.
     */
    protected abstract PrimitiveIterator.OfInt getNonEmptySlots();

    @Override
    public Iterator<ItemStack> getItems() {
        return new ItemHandlerItemStackIterator(itemHandler);
    }

    @Override
    public Iterator<ItemStack> findItems(@Nonnull ItemStack stack, int matchFlags) {
        return Iterators.transform(getSlotsWithItemStack(stack, matchFlags), new Function<Integer, ItemStack>() {
            @Nullable
            @Override
            public ItemStack apply(@Nullable Integer input) {
                return itemHandler.getStackInSlot(input);
            }
        });
    }

    @Override
    @Nonnull
    public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
        PrimitiveIterator.OfInt itNonFull = getNonFullSlotsWithItemStack(stack, ItemMatch.ITEM | ItemMatch.DAMAGE | ItemMatch.NBT);
        while (itNonFull.hasNext() && !stack.isEmpty()) {
            int slot = itNonFull.nextInt();
            stack = itemHandler.insertItem(slot, stack, simulate);
        }

        if (!stack.isEmpty()) {
            PrimitiveIterator.OfInt itEmpty = getEmptySlots();
            while (itEmpty.hasNext() && !stack.isEmpty()) {
                int slot = itEmpty.nextInt();
                stack = itemHandler.insertItem(slot, stack, simulate);
            }
        }

        return stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int amount, boolean simulate) {
        PrimitiveIterator.OfInt it = getNonEmptySlots();
        ItemStack extractedAcc = ItemStack.EMPTY;
        while (it.hasNext() && amount > 0) {
            int slot = it.nextInt();
            ItemStack extractedSimulated = itemHandler.extractItem(slot, amount, true);
            if (!extractedSimulated.isEmpty()) {
                if (extractedAcc.isEmpty()) {
                    ItemStack extracted = simulate ? extractedSimulated : itemHandler.extractItem(slot, amount, false);
                    extractedAcc = extracted.copy();
                    amount -= extracted.getCount();
                } else if (ItemMatch.areItemStacksEqual(extractedSimulated, extractedAcc, ItemMatch.ITEM | ItemMatch.DAMAGE | ItemMatch.NBT)) {
                    ItemStack extracted = simulate ? extractedSimulated : itemHandler.extractItem(slot, amount, false);
                    amount -= extracted.getCount();
                    extractedAcc.grow(extracted.getCount());
                }
            }
        }

        return extractedAcc;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(@Nonnull ItemStack matchStack, int matchFlags, boolean simulate) {
        PrimitiveIterator.OfInt it = getNonEmptySlotsWithItemStack(matchStack, matchFlags);
        int amount = matchStack.getCount();
        ItemStack extractedAcc = ItemStack.EMPTY;
        while (it.hasNext() && amount > 0) {
            int slot = it.nextInt();
            ItemStack extractedSimulated = itemHandler.extractItem(slot, amount, true);
            if (!extractedSimulated.isEmpty()) {
                if (extractedAcc.isEmpty()) {
                    ItemStack extracted = simulate ? extractedSimulated : itemHandler.extractItem(slot, amount, false);
                    extractedAcc = extracted.copy();
                    amount -= extracted.getCount();
                } else if (ItemMatch.areItemStacksEqual(extractedSimulated, extractedAcc, matchFlags & ~ItemMatch.STACKSIZE)) {
                    ItemStack extracted = simulate ? extractedSimulated : itemHandler.extractItem(slot, amount, false);
                    amount -= extracted.getCount();
                    extractedAcc.grow(extracted.getCount());
                }
            }
        }

        return extractedAcc;
    }

    @Override
    public int getLimit() {
        int total = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            total += itemHandler.getSlotLimit(i);
        }
        return total;
    }
}
