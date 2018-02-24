package org.cyclops.commoncapabilities.api.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * A IngredientComponent is a type of component that can be used as ingredients inside recipes.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void. Instances MUST properly implement the equals method.
 * @author rubensworks
 */
public final class IngredientComponent<T, M> implements IForgeRegistryEntry<IngredientComponent<?, ?>> {

    public static final IForgeRegistry<IngredientComponent<?, ?>> REGISTRY = (IForgeRegistry) GameRegistry
            .findRegistry(IngredientComponent.class);

    @GameRegistry.ObjectHolder("minecraft:itemstack")
    public static final IngredientComponent<ItemStack, Integer> ITEMSTACK = null;
    @GameRegistry.ObjectHolder("minecraft:fluidstack")
    public static final IngredientComponent<FluidStack, Integer> FLUIDSTACK = null;
    @GameRegistry.ObjectHolder("minecraft:energy")
    public static final IngredientComponent<Integer, Void> ENERGY = null;

    private final IIngredientMatcher<T, M> matcher;
    private final IIngredientSerializer<T, M> serializer;
    private final T emptyInstance;
    private ResourceLocation name;
    private String unlocalizedName;

    public IngredientComponent(ResourceLocation name, IIngredientMatcher<T, M> matcher, IIngredientSerializer<T, M> serializer, T emptyInstance) {
        this.setRegistryName(name);
        this.matcher = matcher;
        this.serializer = serializer;
        this.emptyInstance = emptyInstance;
    }

    public IngredientComponent(String name, IIngredientMatcher<T, M> matcher, IIngredientSerializer<T, M> serializer, T emptyInstance) {
        this(new ResourceLocation(name), matcher, serializer, emptyInstance);
    }

    public ResourceLocation getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[Recipe Component " + this.name + "]";
    }

    @Override
    public int hashCode() {
        return 45 | this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof IngredientComponent && this.getName().equals(((IngredientComponent) obj).getName()));
    }

    @Override
    public IngredientComponent<T, M> setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    public Class<IngredientComponent<?, ?>> getRegistryType() {
        return (Class) IngredientComponent.class;
    }

    public IngredientComponent<T, M> setUnlocalizedName(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        return this;
    }

    /**
     * @return The unlocalized name.
     */
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    /**
     * @return A matcher instance for comparing instances of this component type.
     */
    public IIngredientMatcher<T, M> getMatcher() {
        return matcher;
    }

    /**
     * @return A (de)serializer instance for this component type.
     */
    public IIngredientSerializer<T, M> getSerializer() {
        return serializer;
    }

    /**
     * @return The instance that acts as an 'empty' instance.
     *         For ItemStacks, this would be ItemStack.EMPTY.
     */
    public T getEmptyInstance() {
        return emptyInstance;
    }
}
