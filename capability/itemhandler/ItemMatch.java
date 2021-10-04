package org.cyclops.commoncapabilities.api.capability.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

import java.util.Comparator;

/**
 * Item matching flags to be used in {@link ISlotlessItemHandler}.
 * @author rubensworks
 */
public final class ItemMatch {

    /**
     * Convenience value matching any ItemStack.
     */
    public static final int ANY = 0;
    /**
     * Match ItemStack items.
     */
    public static final int ITEM = 1;
    /**
     * Match ItemStack NBT tags.
     */
    public static final int NBT = 4;
    /**
     * Match ItemStack stacksizes.
     */
    public static final int STACKSIZE = 8;
    /**
     * Convenience value matching ItemStacks exactly by item, NBT tag and stacksize.
     */
    public static final int EXACT = ITEM | NBT | STACKSIZE;

    /**
     * A comparator for NBT tags. (This is set in GeneralConfig)
     */
    public static Comparator<INBT> NBT_COMPARATOR;

    public static boolean areItemStacksEqual(ItemStack a, ItemStack b, int matchFlags) {
        if (matchFlags == ANY) {
            return true;
        }
        boolean item      = (matchFlags & ITEM     ) > 0;
        boolean nbt       = (matchFlags & NBT      ) > 0;
        boolean stackSize = (matchFlags & STACKSIZE) > 0;
        return a == b || a.isEmpty() && b.isEmpty() ||
                (!a.isEmpty() && !b.isEmpty()
                        && (!item || a.getItem() == b.getItem())
                        && (!stackSize || a.getCount() == b.getCount())
                        && (!nbt || areItemStackTagsEqual(a, b)));
    }

    public static boolean areItemStackTagsEqual(ItemStack a, ItemStack b) {
        CompoundNBT tagA = a.getTag();
        CompoundNBT tagB = b.getTag();
        if (tagA == null && tagB == null) {
            return true;
        } else {
            if (tagA == null) {
                tagA = new CompoundNBT();
            }
            if (tagB == null) {
                tagB = new CompoundNBT();
            }
            return NBT_COMPARATOR.compare(tagA, tagB) == 0;
            // We don't include a.areCapsCompatible(b), because we expect that differing caps have different NBT tags.
        }
    }

}
