package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * A default recipe handler that contains a dirt to diamonds recipe.
 * @author rubensworks
 */
public class DefaultRecipeHandler implements IRecipeHandler {
    @Override
    public Set<RecipeComponent<?, ?>> getRecipeInputComponents() {
        return Sets.newHashSet(RecipeComponent.ITEMSTACK);
    }

    @Override
    public Set<RecipeComponent<?, ?>> getRecipeOutputComponents() {
        return Sets.newHashSet(RecipeComponent.ITEMSTACK);
    }

    @Override
    public boolean isValidSizeInput(RecipeComponent component, int size) {
        return component == RecipeComponent.ITEMSTACK && size == 1;
    }

    @Override
    public List<RecipeDefinition> getRecipes() {
        return Lists.newArrayList(
                new RecipeDefinition(
                        new RecipeIngredients(new RecipeIngredientItemStack(new ItemStack(Blocks.DIRT))),
                        new RecipeIngredients(new RecipeIngredientItemStack(new ItemStack(Items.DIAMOND)))
                )
        );
    }

    @Nullable
    @Override
    public RecipeIngredients simulate(RecipeIngredients input) {
        List<IRecipeIngredient<ItemStack, ItemHandlerRecipeTarget>> ingredients =
                input.getIngredients(RecipeComponent.ITEMSTACK);
        if (input.getIngredientsSize() == 1 && ingredients.size() == 1) {
            if (ingredients.get(0).test(new ItemStack(Blocks.DIRT))) {
                return new RecipeIngredients(new RecipeIngredientItemStack(new ItemStack(Items.DIAMOND)));
            }
        }
        return null;
    }

    @Nullable
    @Override
    public <R> R[] getInputComponentTargets(RecipeComponent<?, R> component) {
        if (component == RecipeComponent.ITEMSTACK) {
            return (R[]) new ItemHandlerRecipeTarget[]{
                    // Just a dummy IItemHandler
                    new ItemHandlerRecipeTarget(new ItemStackHandler(), 0)
            };
        }
        return null;
    }

    @Nullable
    @Override
    public <R> R[] getOutputComponentTargets(RecipeComponent<?, R> component) {
        if (component == RecipeComponent.ITEMSTACK) {
            return (R[]) new ItemHandlerRecipeTarget[]{
                    // Just a dummy IItemHandler
                    new ItemHandlerRecipeTarget(new ItemStackHandler(), 1)
            };
        }
        return null;
    }
}
