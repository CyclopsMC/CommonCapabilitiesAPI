package org.cyclops.commoncapabilities.api.capability.itemhandler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.commoncapabilities.api.capability.resourcehandler.ResourceHandlerIngredientIterator;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * An naive {@link ISlotlessItemHandler} wrapper around an {@link ResourceHandler} for items.
 * This will perform a LIFO item algorithm.
 * @author rubensworks
 */
public class DefaultSlotlessItemHandlerWrapper implements ISlotlessItemHandler {

    private final ResourceHandler<ItemResource> itemHandler;

    public DefaultSlotlessItemHandlerWrapper(ResourceHandler<ItemResource> itemHandler) {
        this.itemHandler = itemHandler;
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return itemHandler;
    }

    @Override
    public Iterator<ItemStack> getItems() {
        return new ResourceHandlerIngredientIterator<>(getItemHandler(), IngredientComponent.ITEMSTACK_CONVERTER);
    }

    @Override
    public Iterator<ItemStack> findItems(@Nonnull ItemStack stack, int matchFlags) {
        return new FilteredItemHandlerItemStackIterator(getItemHandler(), stack, matchFlags);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(@Nonnull ItemStack stack, TransactionContext transaction) {
        int inserted = getItemHandler().insert(ItemResource.of(stack), stack.getCount(), transaction);
        return inserted > 0 ? stack.copyWithCount(stack.getCount() - inserted) : stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int amount, TransactionContext transaction) {
        ResourceHandler<ItemResource> itemHandler = getItemHandler();
        for (int i = 0; i < itemHandler.size(); i++) {
            ItemResource resource = itemHandler.getResource(i);
            if (!resource.isEmpty()) {
                int extracted = itemHandler.extract(i, resource, amount, transaction);
                if (extracted > 0) {
                    return resource.toStack(extracted);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(@Nonnull ItemStack matchStack, int matchFlags, TransactionContext transaction) {
        ResourceHandler<ItemResource> itemHandler = getItemHandler();
        for (int i = 0; i < itemHandler.size(); i++) {
            int amount = matchStack.getCount();
            try (var tx = Transaction.open(transaction)) {
                ItemResource resource = itemHandler.getResource(i);
                if (!resource.isEmpty()) {
                    int extracted = itemHandler.extract(i, resource, amount, tx);
                    ItemStack itemStack = resource.toStack(extracted);
                    if (extracted > 0 && ItemMatch.areItemStacksEqual(matchStack, itemStack, matchFlags)) {
                        tx.commit();
                        return itemStack;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public long getLimit() {
        int total = 0;
        for (int i = 0; i < itemHandler.size(); i++) {
            total += itemHandler.getCapacityAsInt(i, ItemResource.EMPTY);
        }
        return total;
    }
}
