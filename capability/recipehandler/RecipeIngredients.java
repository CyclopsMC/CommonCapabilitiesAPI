package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A collection of recipe component instances.
 * This is typically used to either represent the input or the output of a recipe.
 * @author rubensworks
 */
public class RecipeIngredients {

    private final int size;
    private final Map<RecipeComponent, List<IRecipeIngredient<?, ?>>> ingredientsMap;

    public RecipeIngredients(IRecipeIngredient... recipeIngredients) {
        this.size = recipeIngredients.length;
        this.ingredientsMap = Maps.newIdentityHashMap();
        for (int i = 0; i < recipeIngredients.length; i++) {
            List<IRecipeIngredient<?, ?>> ingredients = this.ingredientsMap.get(recipeIngredients[i].getComponent());
            if (ingredients == null) {
                ingredients = Lists.newLinkedList();
                this.ingredientsMap.put(recipeIngredients[i].getComponent(), ingredients);
            }
            ingredients.add(recipeIngredients[i]);
        }
        for (RecipeComponent recipeComponent : ingredientsMap.keySet()) {
            ingredientsMap.put(recipeComponent, ImmutableList.copyOf(ingredientsMap.get(recipeComponent)));
        }

    }

    public RecipeIngredients(List<IRecipeIngredient> recipeIngredients) {
        this(recipeIngredients.toArray(new IRecipeIngredient[recipeIngredients.size()]));
    }

    /**
     * @return The total amount of ingredients.
     */
    public int getIngredientsSize() {
        return this.size;
    }

    /**
     * @return The recipe component types that are available in this collection.
     */
    public Set<RecipeComponent> getComponents() {
        return this.ingredientsMap.keySet();
    }

    /**
     * All ingredients for the given recipe component type.
     * @param component A component type.
     * @param <T> The recipe ingredient instance type.
     * @return The ingredients for the given recipe component type.
     */
    public <T, R> List<IRecipeIngredient<T, R>> getIngredients(RecipeComponent<T, R> component) {
        return (List) this.ingredientsMap.getOrDefault(component, Collections.emptyList());
    }

    @Override
    public String toString() {
        return "[RecipeIngredients ingredients: " + ingredientsMap.toString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj ||
                (obj instanceof RecipeIngredients
                        && this.ingredientsMap.equals(((RecipeIngredients) obj).ingredientsMap));

    }
}
