package org.cyclops.commoncapabilities.api.capability.inventorystate;

import java.util.Collection;

/**
 * An inventory state capability that combines hashes of several other inventory states.
 * @author rubensworks
 */
public class CombinedInventoryState implements IInventoryState {

    private final IInventoryState[] inventoryStates;

    public CombinedInventoryState(Collection<IInventoryState> inventoryStates) {
        this.inventoryStates = inventoryStates.toArray(new IInventoryState[inventoryStates.size()]);
    }

    @Override
    public int getHash() {
        int hash = 0;
        for (IInventoryState inventoryState : inventoryStates) {
            hash += inventoryState.getHash();
        }
        return hash;
    }
}
