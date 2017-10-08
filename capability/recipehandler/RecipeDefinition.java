package org.cyclops.commoncapabilities.api.capability.recipehandler;

/**
 * A RecipeDefinition defines the input and output of a recipe.
 * @author rubensworks
 */
public class RecipeDefinition {

    private final RecipeIngredients input;
    private final RecipeIngredients output;

    public RecipeDefinition(RecipeIngredients input, RecipeIngredients output) {
        this.input = input;
        this.output = output;
    }

    public RecipeDefinition(IRecipeIngredient[] input, IRecipeIngredient[] output) {
        this(new RecipeIngredients(input), new RecipeIngredients(output));
    }

    /**
     * @return The recipe input.
     */
    public RecipeIngredients getInput() {
        return input;
    }

    /**
     * @return The recipe output.
     */
    public RecipeIngredients getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "[RecipeDefinition input: " + getInput().toString() + "; output: " + getOutput().toString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RecipeDefinition
                && getInput().equals(((RecipeDefinition) obj).getInput())
                && getOutput().equals(((RecipeDefinition) obj).getOutput());
    }
}
