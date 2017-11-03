package org.cyclops.commoncapabilities.api.capability.recipehandler;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * A RecipeComponent is a type of component that can be used inside recipes.
 *
 * @param <T> The instance type.
 * @param <R> The recipe target type, may be Void.
 * @author rubensworks
 */
public final class RecipeComponent<T, R> implements IForgeRegistryEntry<RecipeComponent<?, ?>> {

    public static final IForgeRegistry<RecipeComponent<?, ?>> REGISTRY = (IForgeRegistry) GameRegistry
            .findRegistry(RecipeComponent.class);

    @GameRegistry.ObjectHolder("minecraft:itemstack")
    public static final RecipeComponent<ItemStack, ItemHandlerRecipeTarget>   ITEMSTACK  = null;
    @GameRegistry.ObjectHolder("minecraft:fluidstack")
    public static final RecipeComponent<FluidStack, FluidHandlerRecipeTarget> FLUIDSTACK = null;
    @GameRegistry.ObjectHolder("minecraft:energy")
    public static final RecipeComponent<Integer, IEnergyStorage>              ENERGY     = null;

    private ResourceLocation name;
    private String unlocalizedName;

    public RecipeComponent(ResourceLocation name) {
        this.setRegistryName(name);
    }

    public RecipeComponent(String name) {
        this(new ResourceLocation(name));
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
        return this == obj || (obj instanceof RecipeComponent && this.getName().equals(((RecipeComponent) obj).getName()));
    }

    @Override
    public RecipeComponent<T, R> setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    public Class<RecipeComponent<?, ?>> getRegistryType() {
        return (Class) RecipeComponent.class;
    }

    public RecipeComponent<T, R> setUnlocalizedName(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        return this;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }
}
