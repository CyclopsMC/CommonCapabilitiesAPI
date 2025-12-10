package org.cyclops.commoncapabilities.api.capability.itemhandler;

import com.google.common.collect.Iterators;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.commoncapabilities.api.capability.resourcehandler.ResourceHandlerIngredientIterator;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.PrimitiveIterator;

/**
 * An abstract {@link ISlotlessItemHandler} wrapper around an {@link ResourceHandler} for items.
 * @author rubensworks
 */
public abstract class SlotlessItemHandlerWrapper implements ISlotlessItemHandler {

    protected final ResourceHandler<ItemResource> itemHandler;

    public SlotlessItemHandlerWrapper(ResourceHandler<ItemResource> itemHandler) {
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
        return new ResourceHandlerIngredientIterator<>(itemHandler, IngredientComponent.ITEMSTACK_CONVERTER);
    }

    @Override
    public Iterator<ItemStack> findItems(@Nonnull ItemStack stack, int matchFlags) {
        return Iterators.transform(getSlotsWithItemStack(stack, matchFlags), input -> itemHandler.getResource(input).toStack(itemHandler.getAmountAsInt(input)));
    }

    @Override
    @Nonnull
    public ItemStack insertItem(@Nonnull ItemStack stack, TransactionContext transaction) {
        // First, insert into slots that already contain this item
        PrimitiveIterator.OfInt itNonFull = getNonFullSlotsWithItemStack(stack, ItemMatch.ITEM | ItemMatch.DATA);
        ItemResource resource = ItemResource.of(stack);
        int toInsert = stack.getCount();
        while (itNonFull.hasNext() && toInsert > 0) {
            int slot = itNonFull.nextInt();
            int inserted = itemHandler.insert(slot, resource, toInsert, transaction);
            toInsert -= inserted;
        }

        // Second, insert into empty slots
        PrimitiveIterator.OfInt itEmpty = getEmptySlots();
        while (itEmpty.hasNext() && toInsert > 0) {
            int slot = itEmpty.nextInt();
            int inserted = itemHandler.insert(slot, resource, toInsert, transaction);
            toInsert -= inserted;
        }

        return toInsert == 0 ? ItemStack.EMPTY : stack.copyWithCount(toInsert);
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int amount, TransactionContext transaction) {
        // First, extract from non-empty slots.
        PrimitiveIterator.OfInt it = getNonEmptySlots();
        ItemStack extractedAcc = ItemStack.EMPTY;
        while (it.hasNext() && amount > 0) {
            int slot = it.nextInt();
            ItemResource slotResource = itemHandler.getResource(slot);
            if (extractedAcc.isEmpty() || slotResource.matches(extractedAcc)) {
                try (var tx = Transaction.open(transaction)) {
                    int extracted = itemHandler.extract(slot, slotResource, amount, tx);
                    if (extracted > 0) {
                        tx.commit();
                        if (extractedAcc.isEmpty()) {
                            extractedAcc = slotResource.toStack(extracted);
                        } else {
                            extractedAcc.grow(extracted);
                        }
                        amount -= extracted;
                    }
                }
            }
        }
        return extractedAcc;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(@Nonnull ItemStack matchStack, int matchFlags, TransactionContext transaction) {
        // First, extract from non-empty slots that contain the item.
        PrimitiveIterator.OfInt itSimulated = getNonEmptySlotsWithItemStack(matchStack, matchFlags);
        int amount = matchStack.getCount();
        ItemStack extractedAcc = ItemStack.EMPTY;
        while (itSimulated.hasNext() && amount > 0) {
            int slot = itSimulated.nextInt();
            ItemResource slotResource = itemHandler.getResource(slot);
            if (extractedAcc.isEmpty() || slotResource.matches(extractedAcc)) {
                try (var tx = Transaction.open(transaction)) {
                    int extracted = itemHandler.extract(slot, slotResource, amount, tx);
                    if (extracted > 0) {
                        ItemStack extractedStack = matchStack.copyWithCount(extracted);
                        if (ItemMatch.areItemStacksEqual(extractedStack, matchStack, matchFlags & ~ItemMatch.STACKSIZE)) {
                            tx.commit();
                            if (extractedAcc.isEmpty()) {
                                extractedAcc = slotResource.toStack(extracted);
                            } else {
                                extractedAcc.grow(extracted);
                            }
                            amount -= extracted;
                        }
                    }
                }
            }
        }
        return extractedAcc;
    }

    @Override
    public int getLimit() {
        int total = 0;
        for (int i = 0; i < itemHandler.size(); i++) {
            total += itemHandler.getCapacityAsInt(i, itemHandler.getResource(i));
        }
        return total;
    }
}
