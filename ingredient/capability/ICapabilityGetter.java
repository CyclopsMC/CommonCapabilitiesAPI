package org.cyclops.commoncapabilities.api.ingredient.capability;

import net.neoforged.neoforge.capabilities.BaseCapability;

import javax.annotation.Nullable;

/**
 * Abstraction over Items, Entities, and Levels.
 * @author rubensworks
 */
public interface ICapabilityGetter<C> {

    @Nullable
    <T> T getCapability(BaseCapability<T, C> capability, @Nullable C context);

}
