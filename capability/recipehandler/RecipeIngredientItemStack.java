package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * An ItemStack recipe ingredient wrapper around {@link Ingredient}.
 * @author rubensworks
 */
public class RecipeIngredientItemStack implements IRecipeIngredient<ItemStack, ItemHandlerRecipeTarget> {

    private final Ingredient ingredient;

    public RecipeIngredientItemStack(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public RecipeIngredientItemStack(ItemStack itemStack) {
        this(itemStack, false);
    }

    public RecipeIngredientItemStack(ItemStack itemStack, boolean matchNbt) {
        this.ingredient = matchNbt ? new IngredientNBT(itemStack) : Ingredient.fromStacks(itemStack);
    }

    @Override
    public RecipeComponent<ItemStack, ItemHandlerRecipeTarget> getComponent() {
        return RecipeComponent.ITEMSTACK;
    }

    @Override
    public List<ItemStack> getMatchingInstances() {
        return Lists.newArrayList(ingredient.getMatchingStacks());
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ingredient.test(itemStack);
    }

    @Override
    public String toString() {
        return "[RecipeIngredientItemStack ingredient: " + Arrays.toString(ingredient.getMatchingStacks()) + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RecipeIngredientItemStack)) {
            return false;
        }

        ItemStack[] a = this.ingredient.getMatchingStacks();
        ItemStack[] b = ((RecipeIngredientItemStack) obj).ingredient.getMatchingStacks();

        int length = a.length;
        if (b.length != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!ItemStack.areItemStacksEqual(a[i], b[i])) {
                return false;
            }
        }

        return true;
    }

    public static class IngredientNBT extends Ingredient {

        private final ItemStack stack;

        public IngredientNBT(ItemStack stack) {
            super(stack);
            this.stack = stack;
        }

        @Override
        public boolean apply(@Nullable ItemStack stack) {
            return super.apply(stack) && (stack == null || ItemStack.areItemStackTagsEqual(this.stack, stack));
        }
    }
}
