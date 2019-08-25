package org.cyclops.commoncapabilities.api.ingredient;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * An ingredient that is identified by a given instance and can be matched with other instances under a given condition.
 *
 * Implementing classes should properly implement the equals and hashCode methods.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void.
 * @author rubensworks
 */
public interface IPrototypedIngredient<T, M> extends Comparable<IPrototypedIngredient<?, ?>> {

    /**
     * @return The type of ingredient component this prototype exists for.
     */
    public IngredientComponent<T, M> getComponent();

    /**
     * @return The prototype of this ingredient.
     */
    public T getPrototype();

    /**
     * @return The condition under which the prototype instance can be matched.
     */
    public M getCondition();

    /**
     * Deserialize an ingredient to NBT.
     * @param prototypedIngredient Ingredient.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter, may be Void.
     * @return An NBT representation of the given ingredient.
     */
    public static <T, M> CompoundNBT serialize(IPrototypedIngredient<T, M> prototypedIngredient) {
        CompoundNBT tag = new CompoundNBT();

        IngredientComponent<T, M> component = prototypedIngredient.getComponent();
        tag.putString("ingredientComponent", component.getName().toString());

        IIngredientSerializer<T, M> serializer = component.getSerializer();
        tag.put("prototype", serializer.serializeInstance(prototypedIngredient.getPrototype()));
        tag.put("condition", serializer.serializeCondition(prototypedIngredient.getCondition()));

        return tag;
    }

    /**
     * Deserialize an ingredient from NBT
     * @param tag An NBT tag.
     * @return A new ingredient instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given ingredient.
     */
    public static PrototypedIngredient deserialize(CompoundNBT tag) throws IllegalArgumentException {
        if (!tag.contains("ingredientComponent", Constants.NBT.TAG_STRING)) {
            throw new IllegalArgumentException("Could not find a ingredientComponent entry in the given tag");
        }
        if (!tag.contains("prototype")) {
            throw new IllegalArgumentException("Could not find a prototype entry in the given tag");
        }
        if (!tag.contains("condition")) {
            throw new IllegalArgumentException("Could not find a condition entry in the given tag");
        }

        String componentName = tag.getString("ingredientComponent");
        IngredientComponent<?, ?> component = IngredientComponent.REGISTRY.getValue(new ResourceLocation(componentName));
        if (component == null) {
            throw new IllegalArgumentException("Could not find the ingredient component type " + componentName);
        }

        IIngredientSerializer serializer = component.getSerializer();
        Object prototype = serializer.deserializeInstance(tag.get("prototype"));
        Object condition = serializer.deserializeCondition(tag.get("condition"));

        return new PrototypedIngredient(component, prototype, condition);
}

}
