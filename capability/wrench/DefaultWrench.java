package org.cyclops.commoncapabilities.api.capability.wrench;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Default implementation of a {@link IWrench}.
 * @author rubensworks
 */
public class DefaultWrench implements IWrench {
    @Override
    public boolean canUse(EntityPlayer player, WrenchTarget target) {
        return true;
    }

    @Override
    public void beforeUse(EntityPlayer player, WrenchTarget target) {

    }

    @Override
    public void afterUse(EntityPlayer player, WrenchTarget target) {

    }
}
