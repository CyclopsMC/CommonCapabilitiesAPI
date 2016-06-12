package org.cyclops.commoncapabilities.api.capability.worldnameable;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IWorldNameable;

/**
 * Default implementation of a {@link net.minecraft.world.IWorldNameable}
 * @author rubensworks
 */
public class DefaultWorldNameable implements IWorldNameable {

    private ITextComponent name;

    public DefaultWorldNameable() {
        this.name = null;
    }

    public DefaultWorldNameable(String name) {
        setName(name);
    }

    /**
     * Set the name.
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = new TextComponentString(name);
    }

    @Override
    public String getName() {
        if(this.name == null) {
            return "";
        }
        return this.name.getUnformattedComponentText();
    }

    @Override
    public boolean hasCustomName() {
        return this.name != null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.name;
    }
}
