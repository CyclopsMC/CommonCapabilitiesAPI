package org.cyclops.commoncapabilities.api.ingredient;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A collection of ingredient instances of different types.
 * @author rubensworks
 */
public interface IMixedIngredients extends Comparable<IMixedIngredients> {

    /**
     * @return The ingredient component types.
     */
    public Set<IngredientComponent<?, ?>> getComponents();

    /**
     * Get the instances of a certain type.
     * @param ingredientComponent An ingredient component type.
     * @param <T> The instance type.
     * @return Instances.
     */
    public <T> List<T> getInstances(IngredientComponent<T, ?> ingredientComponent);

    /**
     * Get the first non-empty instance of the given type.
     * @param ingredientComponent An ingredient component type.
     * @param <T> The instance type.
     * @return The first non-empty instance, or the empty instance if none could be found.
     */
    public default <T> T getFirstNonEmpty(IngredientComponent<T, ?> ingredientComponent) {
        IIngredientMatcher<T, ?> matcher = ingredientComponent.getMatcher();
        for (T instance : getInstances(ingredientComponent)) {
            if (!matcher.isEmpty(instance)) {
                return instance;
            }
        }
        return matcher.getEmptyInstance();
    }

    /**
     * Check if at least all ingredients from the supplied mixed ingredients are contained in this mixed ingredients.
     * This mixed ingredients could contain more ingredients.
     * @param that The ingredients to look for.
     * @return If at least all ingredients in that are contained in this.
     */
    public default boolean containsAll(IMixedIngredients that) {
        if (!this.getComponents().containsAll(that.getComponents())) {
            return false;
        }

        for (IngredientComponent<?, ?> component : that.getComponents()) {
            List<?> thisInstances = Lists.newArrayList(this.getInstances(component));
            List<?> thatInstances = that.getInstances(component);
            IIngredientMatcher matcher = component.getMatcher();

            for (Object thatInstance : thatInstances) {
                boolean found = false;
                Iterator<?> it = thisInstances.iterator();
                while (it.hasNext() && !found) {
                    if (matcher.matchesExactly(thatInstance, it.next())) {
                        found = true;
                        it.remove();
                    }
                }

                if (!found) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @return If at least one non-empty ingredient is present.
     */
    public default boolean isEmpty() {
        for (IngredientComponent<?, ?> component : getComponents()) {
            IIngredientMatcher matcher = component.getMatcher();
            for (Object instance : getInstances(component)) {
                if (!matcher.isEmpty(instance)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deserialize ingredients to NBT.
     *
     * @param valueOutput The value output.
     * @param ingredients Ingredients.
     */
    public static void serialize(ValueOutput valueOutput, IMixedIngredients ingredients) {
        for (IngredientComponent<?, ?> component : ingredients.getComponents()) {
            ValueOutput.ValueOutputList instances = valueOutput.childrenList(IngredientComponent.REGISTRY.getKey(component).toString());
            IIngredientSerializer serializer = component.getSerializer();
            for (Object instance : ingredients.getInstances(component)) {
                serializer.serializeInstance(instances.addChild(), instance);
            }
        }
    }

    /**
     * Deserialize ingredients from NBT
     *
     * @param valueInput The value input.
     * @return A new mixed ingredients instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given ingredients.
     */
    public static MixedIngredients deserialize(ValueInput valueInput) throws IllegalArgumentException {
        Map<IngredientComponent<?, ?>, List<?>> ingredients = Maps.newIdentityHashMap();
        for (String componentName : valueInput.keySet()) {
            IngredientComponent<?, ?> component = IngredientComponent.REGISTRY.get(ResourceLocation.parse(componentName))
                    .orElseThrow(() -> new IllegalArgumentException("Could not find the ingredient component type " + componentName))
                    .value();
            ValueInput.ValueInputList instancesTag = valueInput.childrenList(componentName)
                    .orElseThrow(() -> new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid list of instances"));
            IIngredientSerializer serializer = component.getSerializer();
            List instances = Lists.newArrayList();
            for (ValueInput instanceTag : instancesTag) {
                instances.add(serializer.deserializeInstance(instanceTag));
            }
            ingredients.put(component, instances);
        }
        return new MixedIngredients(ingredients);
    }

}
