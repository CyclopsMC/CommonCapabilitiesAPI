package org.cyclops.commoncapabilities.api.capability.itemhandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

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
    public static final int TAG = 4;
    /**
     * Match ItemStack stacksizes.
     */
    public static final int STACKSIZE = 8;
    /**
     * Convenience value matching ItemStacks exactly by item, NBT tag and stacksize.
     */
    public static final int EXACT = ITEM | TAG | STACKSIZE;

    /**
     * A comparator for NBT tags. (This is set in GeneralConfig)
     */
    public static Comparator<Tag> TAG_COMPARATOR;

    public static boolean areItemStacksEqual(ItemStack a, ItemStack b, int matchFlags) {
        if (matchFlags == ANY) {
            return true;
        }
        boolean item      = (matchFlags & ITEM     ) > 0;
        boolean nbt       = (matchFlags & TAG) > 0;
        boolean stackSize = (matchFlags & STACKSIZE) > 0;
        return a == b || a.isEmpty() && b.isEmpty() ||
                (!a.isEmpty() && !b.isEmpty()
                        && (!item || a.getItem() == b.getItem())
                        && (!stackSize || a.getCount() == b.getCount())
                        && (!nbt || areItemStackTagsEqual(a, b)));
    }

    public static boolean areItemStackTagsEqual(ItemStack a, ItemStack b) {
        CompoundTag tagA = a.getTag();
        CompoundTag tagB = b.getTag();
        if (tagA == null && tagB == null) {
            return true;
        } else {
            if (tagA == null) {
                tagA = new CompoundTag();
            }
            if (tagB == null) {
                tagB = new CompoundTag();
            }
            return TAG_COMPARATOR.compare(tagA, tagB) == 0;
            // We don't include a.areCapsCompatible(b), because we expect that differing caps have different NBT tags.
        }
    }

}
