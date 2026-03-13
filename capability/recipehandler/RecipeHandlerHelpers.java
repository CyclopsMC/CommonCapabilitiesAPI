package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.commoncapabilities.api.ingredient.IPrototypedIngredient;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author rubensworks
 */
public class RecipeHandlerHelpers {

    /**
     * A heuristical method for converting an ingredient to a list of prototyped ingredients.
     * @param ingredient An ingredient.
     * @return A list of prototyped ingredients.
     */
    public static List<IPrototypedIngredient<ItemStack, Integer>> getPrototypesFromIngredient(Ingredient ingredient) {
        if (ingredient.isCustom() && ingredient.getCustomIngredient() instanceof CompoundIngredient compoundIngredient) {
            return Lists.newArrayList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK,
                    compoundIngredient.getItems().findFirst().get(), ItemMatch.ITEM | ItemMatch.DATA));
//        } else if (ingredient instanceof OreIngredient) { // TODO: somehow detect tags in the future, see ShapelessRecipeBuilder
//            return Arrays.stream(ingredient.getMatchingStacks())
//                    .map(itemStack -> new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, itemStack, ItemMatch.ITEM))
//                    .collect(Collectors.toList());
        } else {
            return Arrays.stream(ingredient.getItems())
                    .map(itemStack -> new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, itemStack, ItemMatch.ITEM))
                    .collect(Collectors.toList());
        }
    }

    public static List<IPrototypedIngredient<ItemStack, Integer>> getPrototypesFromIngredient(SizedIngredient ingredient) {
        return Lists.newArrayList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK,
                ingredient.getItems()[0], ItemMatch.ITEM | ItemMatch.DATA));
    }

    @Nullable
    public static <C extends RecipeInput, T extends Recipe<C>> RecipeDefinition recipeToRecipeDefinition(ResourceLocation recipeId, T recipe, HolderLookup.Provider lookupProvider) {
        if (recipe.getResultItem(lookupProvider).isEmpty()) {
            return null;
        }
        int inputSize = recipe.getIngredients().size();
        List<List<IPrototypedIngredient<ItemStack, Integer>>> inputIngredients;
        if (inputSize == 0) {
            return null;
        }

        if (recipe instanceof ShapedRecipe shapedRecipe) {
            inputIngredients = Lists.newArrayListWithCapacity(9);
            // We keep the grid shape for shaped recipes
            for (int h = 0; h < 3; h++) {
                for (int w = 0; w < 3; w++) {
                    if (h < shapedRecipe.getHeight() && w < shapedRecipe.getWidth()) {
                        inputIngredients.add(getRecipeInputPrototypes(recipe, w + h * shapedRecipe.getWidth()));
                    } else {
                        inputIngredients.add(Lists.newArrayList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, ItemStack.EMPTY, ItemMatch.ITEM)));
                    }
                }
            }
        } else {
            // Shapeless
            inputIngredients = Lists.newArrayListWithCapacity(inputSize);
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                inputIngredients.add(i, getRecipeInputPrototypes(recipe, i));
            }
        }
        return RecipeDefinition.ofIngredients(IngredientComponent.ITEMSTACK, inputIngredients,
                MixedIngredients.ofInstance(IngredientComponent.ITEMSTACK, recipe.getResultItem(lookupProvider)), recipeId);
    }

    public static <C extends RecipeInput, T extends Recipe<C>> List<IPrototypedIngredient<ItemStack, Integer>> getRecipeInputPrototypes(T recipe, int index) {
        Ingredient ingredient = recipe.getIngredients().get(index);
        List<IPrototypedIngredient<ItemStack, Integer>> prototypes = getPrototypesFromIngredient(ingredient);
        if (prototypes.isEmpty()) {
            prototypes.add(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, ItemStack.EMPTY, ItemMatch.ITEM));
        }
        return prototypes;
    }

}
