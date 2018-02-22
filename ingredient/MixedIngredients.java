package org.cyclops.commoncapabilities.api.ingredient;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Raw implementation of mixed ingredients.
 * @author rubensworks
 */
public class MixedIngredients implements IMixedIngredients {

    private final Map<IngredientComponent<?, ?, ?>, List<?>> ingredients;

    public MixedIngredients(Map<IngredientComponent<?, ?, ?>, List<?>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public Set<IngredientComponent<?, ?, ?>> getComponents() {
        return ingredients.keySet();
    }

    @Override
    public <T> List<T> getInstances(IngredientComponent<T, ?, ?> ingredientComponent) {
        return (List<T>) ingredients.getOrDefault(ingredientComponent, Collections.emptyList());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IMixedIngredients) {
            IMixedIngredients that = (IMixedIngredients) obj;
            if (Sets.newHashSet(this.getComponents()).equals(Sets.newHashSet(that.getComponents()))) {
                for (IngredientComponent<?, ?, ?> component : getComponents()) {
                    List<?> thisInstances = this.getInstances(component);
                    List<?> thatInstances = that.getInstances(component);
                    IIngredientMatcher matcher = component.getMatcher();
                    if (thisInstances.size() == thatInstances.size()) {
                        for (int i = 0; i < thisInstances.size(); i++) {
                            if (!matcher.matchesExactly(thisInstances.get(i), thatInstances.get(i))) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 902;
        for (IngredientComponent<?, ?, ?> component : getComponents()) {
            hash |= component.hashCode() << 2;
            IIngredientMatcher matcher = component.getMatcher();
            for (Object instance : getInstances(component)) {
                hash |= matcher.hash(instance);
            }
        }
        return hash;
    }

    @Override
    public String toString() {
        return "[MixedIngredients ingredients: " + ingredients.toString() + "]";
    }

    public static <T> MixedIngredients ofInstances(IngredientComponent<T, ?, ?> component, Collection<T> instances) {
        Map<IngredientComponent<?, ?, ?>, List<?>> ingredients = Maps.newIdentityHashMap();
        ingredients.put(component, instances instanceof ArrayList ? (List<?>) instances : Lists.newArrayList(instances));
        return new MixedIngredients(ingredients);
    }

    /**
     * Create a new ingredients for a single instance.
     * @param component A component type.
     * @param instance An instance.
     * @param <T> The instance type.
     * @return New ingredients.
     */
    public static <T> MixedIngredients ofInstance(IngredientComponent<T, ?, ?> component, T instance) {
        return ofInstances(component, Lists.newArrayList(instance));
    }

    /**
     * Create ingredients from the given recipe's input.
     * This will create the ingredients based on the first prototype of the recipe's inputs.
     * @param recipe A recipe.
     * @return New ingredients.
     */
    public static MixedIngredients fromRecipeInput(IRecipeDefinition recipe) {
        Map<IngredientComponent<?, ?, ?>, List<?>> ingredients = Maps.newIdentityHashMap();
        for (IngredientComponent<?, ?, ?> component : recipe.getInputComponents()) {
            List<?> instances = recipe.getInputs(component).stream()
                    .map(ingredient -> {
                        IPrototypedIngredient<?, ?, ?> firstIngredient = Iterables.getFirst(ingredient, null);
                        if (firstIngredient == null) {
                            return component.getEmptyInstance();
                        }
                        return firstIngredient.getPrototype();
                    })
                    .collect(Collectors.toList());
            ingredients.put(component, instances);
        }
        return new MixedIngredients(ingredients);
    }
}
