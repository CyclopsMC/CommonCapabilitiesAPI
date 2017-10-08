package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.Collection;

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
        this.ingredient = Ingredient.fromStacks(itemStack);
    }

    @Override
    public RecipeComponent<ItemStack, ItemHandlerRecipeTarget> getComponent() {
        return RecipeComponent.ITEMSTACK;
    }

    @Override
    public Collection<ItemStack> getMatchingInstances() {
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
}
