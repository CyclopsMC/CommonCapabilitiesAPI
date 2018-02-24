package org.cyclops.commoncapabilities.api.ingredient;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A collection of ingredient instances of different types.
 * @author rubensworks
 */
public interface IMixedIngredients {

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
     * Deserialize ingredients to NBT.
     * @param ingredients Ingredients.
     * @return An NBT representation of the given ingredients.
     */
    public static NBTTagCompound serialize(IMixedIngredients ingredients) {
        NBTTagCompound tag = new NBTTagCompound();
        for (IngredientComponent<?, ?> component : ingredients.getComponents()) {
            NBTTagList instances = new NBTTagList();
            IIngredientSerializer serializer = component.getSerializer();
            for (Object instance : ingredients.getInstances(component)) {
                instances.appendTag(serializer.serializeInstance(instance));
            }
            tag.setTag(component.getRegistryName().toString(), instances);
        }
        return tag;
    }

    /**
     * Deserialize ingredients from NBT
     * @param tag An NBT tag.
     * @return A new mixed ingredients instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given ingredients.
     */
    public static MixedIngredients deserialize(NBTTagCompound tag) throws IllegalArgumentException {
        Map<IngredientComponent<?, ?>, List<?>> ingredients = Maps.newIdentityHashMap();
        for (String componentName : tag.getKeySet()) {
            IngredientComponent<?, ?> component = IngredientComponent.REGISTRY.getValue(new ResourceLocation(componentName));
            if (component == null) {
                throw new IllegalArgumentException("Could not find the ingredient component type " + componentName);
            }
            NBTBase subTag = tag.getTag(componentName);
            if (!(subTag instanceof NBTTagList)) {
                throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid list of instances");
            }
            NBTTagList instancesTag = (NBTTagList) subTag;
            IIngredientSerializer serializer = component.getSerializer();
            List instances = Lists.newArrayList();
            for (NBTBase instanceTag : instancesTag) {
                instances.add(serializer.deserializeInstance(instanceTag));
            }
            ingredients.put(component, instances);
        }
        return new MixedIngredients(ingredients);
    }

}