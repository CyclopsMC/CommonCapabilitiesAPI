package org.cyclops.commoncapabilities.api.capability.resourcehandler;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.cyclops.commoncapabilities.api.ingredient.IResourceConverter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over all slots in an item handler.
 * @author rubensworks
 */
public class ResourceHandlerIngredientIterator<R extends Resource, T> implements Iterator<T> {

    private final ResourceHandler<R> resourceHandler;
    private final IResourceConverter<R, T> resourceConverter;
    private final int maxSlots;
    private int slot;

    public ResourceHandlerIngredientIterator(ResourceHandler<R> resourceHandler, IResourceConverter<R, T> resourceConverter, int offset) {
        this.resourceHandler = resourceHandler;
        this.resourceConverter = resourceConverter;
        // Cache the total slot count, since it can be an expensive operation on composite inventories
        this.maxSlots = resourceHandler.size();
        this.slot = offset;
    }

    public ResourceHandlerIngredientIterator(ResourceHandler<R> resourceHandler, IResourceConverter<R, T> resourceConverter) {
        this(resourceHandler, resourceConverter, 0);
    }

    @Override
    public boolean hasNext() {
        return slot < maxSlots;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Slot out of bounds");
        }
        return resourceConverter.fromResource(resourceHandler.getResource(slot), resourceHandler.getAmountAsInt(slot++));
    }
}
