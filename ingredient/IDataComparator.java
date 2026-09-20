package org.cyclops.commoncapabilities.api.ingredient;

import net.minecraft.core.component.DataComponentMap;

import java.util.Comparator;

/**
 * A comparator for data component maps that can ignore certain data component types.
 * @author rubensworks
 */
public interface IDataComparator extends Comparator<DataComponentMap> {

    /**
     * @return If at least one data component type is being ignored.
     */
    public boolean hasIgnoreDataComponentTypes();

}
