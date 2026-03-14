package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;
import org.cyclops.cyclopscore.helper.IModHelpers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author rubensworks
 */
public class RecipeHandlerHelpers {

    /**
     * A heuristical method for converting a display slot to a list of prototyped ingredients.
     * @param display A display slot.
     * @return A list of prototyped ingredients.
     */
    public static IPrototypedIngredientAlternatives<ItemStack, Integer> getPrototypesFromDisplay(SlotDisplay display) {
        return getPrototypesFromDisplay(display, 1);
    }

    /**
     * A heuristical method for converting a display slot to a list of prototyped ingredients.
     * @param display A display slot.
     * @param count Override for the prototype counts.
     * @return A list of prototyped ingredients.
     */
    public static IPrototypedIngredientAlternatives<ItemStack, Integer> getPrototypesFromDisplay(SlotDisplay display, int count) {
        if (display instanceof SlotDisplay.TagSlotDisplay(net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tag)) {
            return new PrototypedIngredientAlternativesItemStackTag(Lists.newArrayList(tag.location().toString()), ItemMatch.ITEM, count);
        } else if (display instanceof SlotDisplay.Empty) {
            return new PrototypedIngredientAlternativesList<>(Lists.newArrayList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, ItemStack.EMPTY, ItemMatch.ITEM)));
        } else if (display instanceof SlotDisplay.ItemStackSlotDisplay(ItemStack stack)) {
            return new PrototypedIngredientAlternativesList<>(Lists.newArrayList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, stack, ItemMatch.ITEM | ItemMatch.DATA)));
        } else {
            PrototypedIngredientAlternativesList<ItemStack, Integer> prototypes = new PrototypedIngredientAlternativesList<>(display.resolveForStacks(ContextMap.EMPTY).stream()
                    .map(item -> new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, overrideStackCount(item, count), ItemMatch.ITEM))
                    .collect(Collectors.toList()));
            if (prototypes.getAlternatives().isEmpty()) {
                prototypes = new PrototypedIngredientAlternativesList<>(Lists.newArrayList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, ItemStack.EMPTY, ItemMatch.ITEM)));
            }
            return prototypes;
        }
    }

    /**
     * A heuristical method for converting an ingredient to a list of prototyped ingredients.
     * @param ingredient An ingredient.
     * @return A list of prototyped ingredients.
     */
    public static IPrototypedIngredientAlternatives<ItemStack, Integer> getPrototypesFromIngredient(Ingredient ingredient, @Nullable SlotDisplay display) {
        return getPrototypesFromIngredient(ingredient, display, 1);
    }

    /**
     * A heuristical method for converting an ingredient to a list of prototyped ingredients.
     * @param ingredient An ingredient.
     * @param count Override for the prototype counts.
     * @return A list of prototyped ingredients.
     */
    public static IPrototypedIngredientAlternatives<ItemStack, Integer> getPrototypesFromIngredient(Ingredient ingredient, @Nullable SlotDisplay display, int count) {
        if (display != null) {
            return getPrototypesFromDisplay(display, count);
        } else if (ingredient.isCustom()) {
            return new PrototypedIngredientAlternativesList<>(Lists.newArrayList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK,
                    overrideStackCount(new ItemStack(ingredient.getCustomIngredient().items().findFirst().get().value()), count), ItemMatch.ITEM | ItemMatch.DATA)));
        } else {
            PrototypedIngredientAlternativesList<ItemStack, Integer> prototypes = new PrototypedIngredientAlternativesList<>(ingredient.getValues().stream()
                    .map(item -> new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, overrideStackCount(new ItemStack(item), count), ItemMatch.ITEM))
                    .collect(Collectors.toList()));
            if (prototypes.getAlternatives().isEmpty()) {
                prototypes = new PrototypedIngredientAlternativesList<>(Lists.newArrayList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, ItemStack.EMPTY, ItemMatch.ITEM)));
            }
            return prototypes;
        }
    }

    protected static ItemStack overrideStackCount(ItemStack stack, int count) {
        if (count > 1) {
            stack = stack.copy();
            stack.setCount(count);
        }
        return stack;
    }

    public static IPrototypedIngredientAlternatives<ItemStack, Integer> getPrototypesFromIngredient(SizedIngredient ingredient) {
        return getPrototypesFromIngredient(ingredient.ingredient(), null, ingredient.count());
    }

    @Nullable
    public static <C extends RecipeInput, T extends Recipe<C>> RecipeDefinition recipeToRecipeDefinition(ResourceKey<Recipe<?>> recipeId, T recipe, Level level) {
        ItemStack recipeOutput = IModHelpers.get().getMinecraftHelpers().getRecipeOutput(recipe, level);
        if (recipeOutput.isEmpty()) {
            return null;
        }

        List<Ingredient> ingredients = recipe.placementInfo().ingredients();
        int inputSize = ingredients.size();
        List<IPrototypedIngredientAlternatives<ItemStack, Integer>> inputIngredients;
        if (inputSize == 0) {
            return null;
        }
        RecipeDisplay recipeDisplay = recipe.display().get(0);

        if (recipe instanceof ShapedRecipe) {
            int width = 3;
            int height = 3;
            inputIngredients = Lists.newArrayListWithCapacity(9);
            for (int i = 0; i < width * height; i++) {
                inputIngredients.add(new PrototypedIngredientAlternativesList<>(Lists.newArrayList(
                        new PrototypedIngredient<>(IngredientComponents.ITEMSTACK, ItemStack.EMPTY, ItemMatch.ITEM | ItemMatch.DATA)
                )));
            }
            ShapedCraftingRecipeDisplay recipeDisplayShaped = (ShapedCraftingRecipeDisplay) recipeDisplay;
            PlaceRecipeHelper.placeRecipe(width, height, recipeDisplayShaped.width(), recipeDisplayShaped.height(), recipeDisplayShaped.ingredients(), (display, destinationSlot, x, y) -> {
                inputIngredients.set(destinationSlot, getPrototypesFromDisplay(display));
            });
        } else {
            // Shapeless
            inputIngredients = Lists.newArrayListWithCapacity(inputSize);
            List<SlotDisplay> displayIngredients = recipeDisplay instanceof ShapelessCraftingRecipeDisplay recipeDisplayShapeless ? recipeDisplayShapeless.ingredients() : null;
            for (int i = 0; i < ingredients.size(); i++) {
                inputIngredients.add(i, getPrototypesFromIngredient(ingredients.get(i), displayIngredients != null ? displayIngredients.get(i) : null));
            }
        }
        return RecipeDefinition.ofAlternatives(IngredientComponent.ITEMSTACK, inputIngredients,
                MixedIngredients.ofInstance(IngredientComponent.ITEMSTACK, recipeOutput), recipeId);
    }

}
