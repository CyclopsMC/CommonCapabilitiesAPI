package org.cyclops.commoncapabilities.api.capability.recipehandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * An IRecipeHandler is able to process recipes.
 *
 * It is used to expose the possible recipes the target can handle,
 * and simulate what output will be produced based on a certain input.
 *
 * It may also expose further information on how the inputs and outputs
 * must be handled.
 *
 * @author rubensworks
 */
public interface IRecipeHandler {

    /**
     * @return The input recipe component types that are possible for recipes in this handler.
     */
    public Set<RecipeComponent<?, ?>> getRecipeInputComponents();

    /**
     * @return The output recipe component types that are possible for recipes in this handler.
     */
    public Set<RecipeComponent<?, ?>> getRecipeOutputComponents();

    /**
     * Check if the given size of recipe component instances are valid for the given recipe component type.
     * @param component The component type.
     * @param size      A certain length of recipe component instances.
     * @return If the given size of recipe component instances can be used by this recipe handler.
     */
    public boolean isValidSizeInput(RecipeComponent component, int size);

    /**
     * @return Recipes that are available through this handler, this list is not necessarily exhaustive, but SHOULD be.
     */
    public List<RecipeDefinition> getRecipes();

    /**
     * Test if the given recipe input can be handled by this handler.
     * @param input A recipe input.
     * @return The simulated output, or null if no valid recipe for the given input was found.
     */
    @Nullable
    public RecipeIngredients simulate(RecipeIngredients input);

    /**
     * Get the input recipe targets for the given recipe component type.
     * Can be null if the component type is not applicable for this recipe handler,
     * or if no specified targets exist, in this case, the caller should assume default logic for handling.
     * @param component The component type.
     * @param <R> The recipe target type.
     * @return Recipe component targets for each recipe ingredient, or null if not available.
     */
    @Nullable
    public <R> R[] getInputComponentTargets(RecipeComponent<?, R> component);

    /**
     * Get the output recipe targets for the given recipe component type.
     * Can be null if the component type is not applicable for this recipe handler,
     * or if no specified targets exist, in this case, the caller should assume default logic for handling.
     * @param component The component type.
     * @param <R> The recipe target type.
     * @return Recipe component targets for each recipe ingredient, or null if not available.
     */
    @Nullable
    public <R> R[] getOutputComponentTargets(RecipeComponent<?, R> component);

}
