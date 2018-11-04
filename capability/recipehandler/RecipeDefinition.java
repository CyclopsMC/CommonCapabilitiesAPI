package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IPrototypedIngredient;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.cyclopscore.helper.CollectionHelpers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A recipe definition based on maps.
 * @author rubensworks
 */
public class RecipeDefinition implements IRecipeDefinition {

    private final Map<IngredientComponent<?, ?>, List<List<IPrototypedIngredient<?, ?>>>> inputs;
    private final IMixedIngredients output;

    public RecipeDefinition(Map<IngredientComponent<?, ?>, List<List<IPrototypedIngredient<?, ?>>>> inputs,
                            IMixedIngredients output) {
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public Set<IngredientComponent<?, ?>> getInputComponents() {
        return inputs.keySet();
    }

    @Override
    public <T, R, M> List<List<IPrototypedIngredient<T, M>>> getInputs(IngredientComponent<T, M> ingredientComponent) {
        return (List<List<IPrototypedIngredient<T, M>>>) (List) inputs.getOrDefault(ingredientComponent, Collections.emptyList());
    }

    @Override
    public IMixedIngredients getOutput() {
        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IRecipeDefinition) {
            IRecipeDefinition that = (IRecipeDefinition) obj;
            if (Sets.newHashSet(this.getInputComponents()).equals(Sets.newHashSet(that.getInputComponents()))
                    && this.getOutput().equals(that.getOutput())) {
                for (IngredientComponent<?, ?> component : getInputComponents()) {
                    if (!this.getInputs(component).equals(that.getInputs(component))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 578 | inputs.hashCode() << 2 | output.hashCode();
    }

    @Override
    public String toString() {
        return "[RecipeDefinition input: " + inputs.toString() + "; output: " + output.toString() + "]";
    }

    /**
     * Create a new recipe definition for a single component type input and a list of instances.
     * @param component A component type.
     * @param ingredients The ingredients for the given component type.
     * @param output The recipe output.
     * @param <T> The instance type.
     * @param <R> The recipe target type, may be Void.
     * @param <M> The matching condition parameter, may be Void.
     * @return A new recipe definition.
     */
    public static <T, R, M> RecipeDefinition ofIngredients(IngredientComponent<T, M> component,
                                                           List<List<IPrototypedIngredient<T, M>>> ingredients,
                                                           IMixedIngredients output) {
        Map<IngredientComponent<?, ?>, List<List<IPrototypedIngredient<?, ?>>>> inputs = Maps.newIdentityHashMap();
        inputs.put(component, (List) ingredients);
        return new RecipeDefinition(inputs, output);
    }

    /**
     * Create a new recipe definittion for a single component type input and a single instance.
     * @param component A component type.
     * @param ingredient An ingredient for the given component type.
     * @param output The recipe output.
     * @param <T> The instance type.
     * @param <R> The recipe target type, may be Void.
     * @param <M> The matching condition parameter, may be Void.
     * @return A new recipe definition.
     */
    public static <T, R, M> RecipeDefinition ofIngredient(IngredientComponent<T, M> component,
                                                          List<IPrototypedIngredient<T, M>> ingredient,
                                                          IMixedIngredients output) {
        List<List<IPrototypedIngredient<T, M>>> ingredients = Lists.newArrayList();
        ingredients.add(ingredient);
        return ofIngredients(component, ingredients, output);
    }

    @Override
    public int compareTo(IRecipeDefinition that) {
        // Compare output
        int compOutput = this.getOutput().compareTo(that.getOutput());
        if (compOutput != 0) {
            return compOutput;
        }

        // Compare input components
        int compComp = CollectionHelpers.compareCollection(this.getInputComponents(), that.getInputComponents());
        if (compComp != 0) {
            return compComp;
        }

        // Compare input instances
        for (IngredientComponent component : getInputComponents()) {
            List<List<IPrototypedIngredient>> thisInputs = this.getInputs(component);
            List<List<IPrototypedIngredient>> thatInputs = that.getInputs(component);

            if (thisInputs.size() != thatInputs.size()) {
                return thisInputs.size() - thatInputs.size();
            }

            Object[] aArray = thisInputs.toArray();
            Object[] bArray = thatInputs.toArray();
            for (int i = 0; i < aArray.length; i++) {
                int compCol = CollectionHelpers.compareCollection((List<IPrototypedIngredient>) aArray[i], (List<IPrototypedIngredient>) bArray[i]);
                if (compCol != 0) {
                    return compCol;
                }
            }
        }

        return 0;
    }
}
