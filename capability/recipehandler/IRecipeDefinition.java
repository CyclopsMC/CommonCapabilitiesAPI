package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
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
     * @return The output ingredients.
     */
    public IMixedIngredients getOutput();

    /**
     * Deserialize a recipe to NBT.
     * @param recipe A recipe.
     * @return An NBT representation of the given recipe.
     */
    public static CompoundNBT serialize(IRecipeDefinition recipe) {
        CompoundNBT tag = new CompoundNBT();
        CompoundNBT inputTag = new CompoundNBT();
        for (IngredientComponent<?, ?> component : recipe.getInputComponents()) {
            ListNBT instances = new ListNBT();
            for (IPrototypedIngredientAlternatives ingredient : recipe.getInputs(component)) {
                CompoundNBT subTag = new CompoundNBT();
                IPrototypedIngredientAlternatives.ISerializer serializer = ingredient.getSerializer();
                subTag.put("val", serializer.serialize(component, ingredient));
                subTag.putByte("type", serializer.getId());
                instances.add(subTag);
            }
            inputTag.put(component.getRegistryName().toString(), instances);
        }
        tag.put("input", inputTag);
        tag.put("output", IMixedIngredients.serialize(recipe.getOutput()));
        return tag;
    }

    /**
     * Deserialize a recipe from NBT
     * @param tag An NBT tag.
     * @return A new mixed recipe instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given recipe.
     */
    public static RecipeDefinition deserialize(CompoundNBT tag) throws IllegalArgumentException {
        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = Maps.newIdentityHashMap();
        if (!tag.contains("input")) {
            throw new IllegalArgumentException("A recipe tag did not contain a valid input tag");
        }
        if (!tag.contains("output")) {
            throw new IllegalArgumentException("A recipe tag did not contain a valid output tag");
        }
        CompoundNBT inputTag = tag.getCompound("input");
        for (String componentName : inputTag.keySet()) {
            IngredientComponent<?, ?> component = IngredientComponent.REGISTRY.getValue(new ResourceLocation(componentName));
            if (component == null) {
                throw new IllegalArgumentException("Could not find the ingredient component type " + componentName);
            }
            INBT subTag = inputTag.get(componentName);
            if (!(subTag instanceof ListNBT)) {
                throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid list of instances");
            }
            ListNBT instancesTag = (ListNBT) subTag;
            List<IPrototypedIngredientAlternatives<?, ?>> instances = Lists.newArrayList();
            for (INBT instanceTag : instancesTag) {
                IPrototypedIngredientAlternatives.ISerializer alternativeSerializer;
                INBT deserializeTag;
                if (instanceTag instanceof CompoundNBT) {
                    CompoundNBT instanceTagCompound = (CompoundNBT) instanceTag;
                    byte type = instanceTagCompound.getByte("type");
                    alternativeSerializer = IPrototypedIngredientAlternatives.SERIALIZERS.get(type);
                    if (alternativeSerializer == null) {
                        throw new IllegalArgumentException("Could not find a prototyped ingredient alternative serializer for id " + type);
                    }
                    deserializeTag = ((CompoundNBT) instanceTag).get("val");
                } else {
                    throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid reference to instances");
                }
                IPrototypedIngredientAlternatives alternatives = alternativeSerializer.deserialize(component, deserializeTag);
                instances.add(alternatives);
            }
            inputs.put(component, instances);
        }
        IMixedIngredients output = IMixedIngredients.deserialize(tag.getCompound("output"));
        return new RecipeDefinition(inputs, output);
    }

}
