package org.cyclops.commoncapabilities.api.capability.wrench;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

/**
 * Default implementation of a {@link IWrench} that only applies to blocks.
 * @author rubensworks
 */
public class DefaultWrench implements IWrench {
    @Override
    public boolean canUse(Player player, WrenchTarget target) {
        return target.getType() == HitResult.Type.BLOCK;
    }

    @Override
    public void beforeUse(Player player, WrenchTarget target) {

    }

    @Override
    public void afterUse(Player player, WrenchTarget target) {

    }
}
