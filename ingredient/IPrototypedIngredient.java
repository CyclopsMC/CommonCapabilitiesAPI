package org.cyclops.commoncapabilities.api.ingredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
     *
     * @param <T>                  The instance type.
     * @param <M>                  The matching condition parameter, may be Void.
     * @param valueOutput          The value output.
     * @param prototypedIngredient Ingredient.
     */
    public static <T, M> void serialize(ValueOutput valueOutput, IPrototypedIngredient<T, M> prototypedIngredient) {
        IngredientComponent<T, M> component = prototypedIngredient.getComponent();
        valueOutput.putString("ingredientComponent", component.getName().toString());

        IIngredientSerializer<T, M> serializer = component.getSerializer();
        serializer.serializeInstance(valueOutput.child("prototype"), prototypedIngredient.getPrototype());
        valueOutput.store("condition", ExtraCodecs.NBT, serializer.serializeCondition(prototypedIngredient.getCondition()));
    }

    /**
     * Deserialize an ingredient from NBT
     *
     * @param valueInput The value input.
     * @return A new ingredient instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given ingredient.
     */
    public static PrototypedIngredient deserialize(ValueInput valueInput) throws IllegalArgumentException {
        String componentName = valueInput.getString("ingredientComponent")
                .orElseThrow(() -> new IllegalArgumentException("Could not find a ingredientComponent entry in the given tag"));
        IngredientComponent<?, ?> component = IngredientComponent.REGISTRY.get(ResourceLocation.parse(componentName))
                .orElseThrow(() -> new IllegalArgumentException("Could not find the ingredient component type " + componentName))
                .value();

        IIngredientSerializer serializer = component.getSerializer();
        Object prototype = serializer.deserializeInstance(valueInput.child("prototype").orElseThrow());
        Object condition = serializer.deserializeCondition(valueInput.read("condition", ExtraCodecs.NBT).orElseThrow());

        return new PrototypedIngredient(component, prototype, condition);
}

}
