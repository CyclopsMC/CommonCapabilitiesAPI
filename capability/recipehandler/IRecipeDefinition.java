package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Defines the inputs and outputs of a recipe.
 * Inputs are ingredient prototypes for ingredient component types.
 * Outputs are exact instances for ingredient component types.
 *
 * Implementing classes should properly implement the equals and hashCode methods.
 *
 * @author rubensworks
 */
public interface IRecipeDefinition extends Comparable<IRecipeDefinition> {

    /**
     * @return The input ingredient component types.
     */
    public Set<IngredientComponent<?, ?>> getInputComponents();

    /**
     * Get the input prototypes of a certain type.
     *
     * The first list contains a list of ingredients,
     * whereas the deeper second list contains different prototype-based alternatives for the ingredient at this position.
     *
     * @param ingredientComponent An ingredient component type.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter, may be Void.
     * @return Input prototypes.
     */
    public <T, M> List<IPrototypedIngredientAlternatives<T, M>> getInputs(IngredientComponent<T, M> ingredientComponent);

    /**
     * If the input at the given index is reusable.
     * If an ingredient is reusable, this means that a crafting job for this recipe will not (fully) consume this
     * ingredient, and could potentially be reused in later crafting jobs.
     * @param ingredientComponent An ingredient component type.
     * @param index The index of an input, based on the order in {@link #getInputs(IngredientComponent)}.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter, may be Void.
     * @return If the input at this index is reusable.
     */
    public <T, M> boolean isInputReusable(IngredientComponent<T, M> ingredientComponent, int index);

    /**
     * @return The output ingredients.
     */
    public IMixedIngredients getOutput();

    /**
     * Deserialize a recipe to NBT.
     *
     * @param valueOutput The value output.
     * @param recipe         A recipe.
     */
    public static void serialize(ValueOutput valueOutput, IRecipeDefinition recipe) {
        ValueOutput.ValueOutputList inputTag = valueOutput.childrenList("input");
        for (IngredientComponent<?, ?> component : recipe.getInputComponents().stream().sorted().toList()) {
            // Component
            ValueOutput child = inputTag.addChild();
            String componentName = IngredientComponent.REGISTRY.getKey(component).toString();
            child.putString("component", componentName);

            // Instances
            ValueOutput.ValueOutputList instances = child.childrenList("instances");
            List<IPrototypedIngredientAlternatives> inputs = (List) recipe.getInputs(component);
            int[] reusableBytes = new int[inputs.size()];
            int index = 0;
            for (IPrototypedIngredientAlternatives ingredient : inputs) {
                ValueOutput subTag = instances.addChild();
                IPrototypedIngredientAlternatives.ISerializer serializer = ingredient.getSerializer();
                serializer.serialize(subTag.child("val"), component, ingredient);
                subTag.putByte("type", serializer.getId());
                reusableBytes[index] = recipe.isInputReusable(component, index) ? 1 : 0;
                index++;
            }

            // Reusable
            child.putIntArray("reusable", reusableBytes);
        }
        IMixedIngredients.serialize(valueOutput.child("output"), recipe.getOutput());
    }

    /**
     * Deserialize a recipe from NBT
     *
     * @param valueInput The value input.
     * @return A new mixed recipe instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given recipe.
     */
    public static RecipeDefinition deserialize(ValueInput valueInput) throws IllegalArgumentException {
        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = Maps.newIdentityHashMap();
        Map<IngredientComponent<?, ?>, List<Boolean>> inputsReusable = Maps.newIdentityHashMap();

        for (ValueInput child : valueInput.childrenList("input").orElseThrow()) {
            // Component
            String componentName = child.getString("component").orElseThrow();
            IngredientComponent<?, ?> component = IngredientComponent.REGISTRY.get(Identifier.parse(componentName))
                    .orElseThrow(() -> new IllegalArgumentException("Could not find the ingredient component type " + componentName))
                    .value();

            // Instances
            ValueInput.ValueInputList instancesTag = child.childrenList("instances").orElseThrow();
            List<IPrototypedIngredientAlternatives<?, ?>> instances = Lists.newArrayList();
            for (ValueInput instanceTagCompound : instancesTag) {
                byte type = instanceTagCompound.getByteOr("type", (byte) 0);
                IPrototypedIngredientAlternatives.ISerializer alternativeSerializer = IPrototypedIngredientAlternatives.SERIALIZERS.get(type);
                if (alternativeSerializer == null) {
                    throw new IllegalArgumentException("Could not find a prototyped ingredient alternative serializer for id " + type);
                }
                IPrototypedIngredientAlternatives alternatives = alternativeSerializer.deserialize(instanceTagCompound.child("val").orElseThrow(), component);
                instances.add(alternatives);
            }
            inputs.put(component, instances);

            // Reusable
            int[] subTag = child.getIntArray("reusable").orElseThrow();
            List<Boolean> inputReusable = Lists.newArrayList();
            for (int b : subTag) {
                inputReusable.add(b == 1);
            }
            inputsReusable.put(component, inputReusable);
        }

        IMixedIngredients output = IMixedIngredients.deserialize(valueInput.child("output").orElseThrow());

        return new RecipeDefinition(inputs, inputsReusable, output);
    }

}
