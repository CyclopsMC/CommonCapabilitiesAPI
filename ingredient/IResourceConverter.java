package org.cyclops.commoncapabilities.api.ingredient;

import net.neoforged.neoforge.transfer.resource.Resource;

/**
 * @author rubensworks
 */
public interface IResourceConverter<R extends Resource, T> {

    public T fromResource(R resource, int amount);

    public R toResource(T ingredient);

}
