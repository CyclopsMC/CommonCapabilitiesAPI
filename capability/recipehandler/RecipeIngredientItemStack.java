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
}
