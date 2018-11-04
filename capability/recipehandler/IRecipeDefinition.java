package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientSerializer;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IPrototypedIngredient;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;

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
     * @param <R> The recipe target type, may be Void.
     * @param <M> The matching condition parameter, may be Void.
     * @return Input prototypes.
     */
    public <T, R, M> List<List<IPrototypedIngredient<T, M>>> getInputs(IngredientComponent<T, M> ingredientComponent);

    /**
     * @return The output ingredients.
     */
    public IMixedIngredients getOutput();

    /**
     * Deserialize a recipe to NBT.
     * @param recipe A recipe.
     * @return An NBT representation of the given recipe.
     */
    public static NBTTagCompound serialize(IRecipeDefinition recipe) {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound inputTag = new NBTTagCompound();
        for (IngredientComponent<?, ?> component : recipe.getInputComponents()) {
            NBTTagList instances = new NBTTagList();
            IIngredientSerializer serializer = component.getSerializer();
            for (Object ingredient : recipe.getInputs(component)) {
                NBTTagList prototypes = new NBTTagList();
                for (IPrototypedIngredient prototypedIngredient : (List<IPrototypedIngredient>) ingredient) {
                    NBTTagCompound prototypeTag = new NBTTagCompound();
                    prototypeTag.setTag("prototype", serializer.serializeInstance(prototypedIngredient.getPrototype()));
                    prototypeTag.setTag("condition", serializer.serializeCondition(prototypedIngredient.getCondition()));
                    prototypes.appendTag(prototypeTag);
                }
                instances.appendTag(prototypes);
            }
            inputTag.setTag(component.getRegistryName().toString(), instances);
        }
        tag.setTag("input", inputTag);
        tag.setTag("output", IMixedIngredients.serialize(recipe.getOutput()));
        return tag;
    }

    /**
     * Deserialize a recipe from NBT
     * @param tag An NBT tag.
     * @return A new mixed recipe instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given recipe.
     */
    public static RecipeDefinition deserialize(NBTTagCompound tag) throws IllegalArgumentException {
        Map<IngredientComponent<?, ?>, List<List<IPrototypedIngredient<?, ?>>>> inputs = Maps.newIdentityHashMap();
        if (!tag.hasKey("input")) {
            throw new IllegalArgumentException("A recipe tag did not contain a valid input tag");
        }
        if (!tag.hasKey("output")) {
            throw new IllegalArgumentException("A recipe tag did not contain a valid output tag");
        }
        NBTTagCompound inputTag = tag.getCompoundTag("input");
        for (String componentName : inputTag.getKeySet()) {
            IngredientComponent<?, ?> component = IngredientComponent.REGISTRY.getValue(new ResourceLocation(componentName));
            if (component == null) {
                throw new IllegalArgumentException("Could not find the ingredient component type " + componentName);
            }
            NBTBase subTag = inputTag.getTag(componentName);
            if (!(subTag instanceof NBTTagList)) {
                throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid list of instances");
            }
            NBTTagList instancesTag = (NBTTagList) subTag;
            IIngredientSerializer serializer = component.getSerializer();
            List<List<IPrototypedIngredient<?, ?>>> instances = Lists.newArrayList();
            for (NBTBase instanceTag : instancesTag) {
                if (!(instanceTag instanceof NBTTagList)) {
                    throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid sublist of instances");
                }
                NBTTagList prototypesTag = (NBTTagList) instanceTag;
                List prototypes = Lists.newArrayList();
                for (NBTBase prototypeTag : prototypesTag) {
                    if (!(prototypeTag instanceof NBTTagCompound)) {
                        throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid sublist with NBTTagCompunds");
                    }
                    NBTTagCompound safePrototypeTag = (NBTTagCompound) prototypeTag;
                    if (!safePrototypeTag.hasKey("prototype")) {
                        throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid sublist with a prototype entry");
                    }
                    if (!safePrototypeTag.hasKey("condition")) {
                        throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid sublist with a condition entry");
                    }
                    prototypes.add(new PrototypedIngredient(component,
                            serializer.deserializeInstance(safePrototypeTag.getTag("prototype")),
                            serializer.deserializeCondition(safePrototypeTag.getTag("condition"))));
                }
                instances.add(prototypes);
            }
            inputs.put(component, instances);
        }
        IMixedIngredients output = IMixedIngredients.deserialize(tag.getCompoundTag("output"));
        return new RecipeDefinition(inputs, output);
    }

}
