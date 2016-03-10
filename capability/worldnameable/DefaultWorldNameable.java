package org.cyclops.commoncapabilities.api.capability.worldnameable;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IWorldNameable;

/**
 * Default implementation of a {@link net.minecraft.world.IWorldNameable}
 * @author rubensworks
 */
public class DefaultWorldNameable implements IWorldNameable {

    private ChatComponentText name;

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
        this.name = new ChatComponentText(name);
    }

    @Override
    public String getName() {
        if(this.name == null) {
            return "";
        }
        return this.name.getUnformattedTextForChat();
    }

    @Override
    public boolean hasCustomName() {
        return this.name != null;
    }

    @Override
    public IChatComponent getDisplayName() {
        return this.name;
    }
}
