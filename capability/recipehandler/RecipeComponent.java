package org.cyclops.commoncapabilities.api.capability.recipehandler;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;

/**
 * A RecipeComponent is a type of component that can be used inside recipes.
 * A certain component type MUST only be created once.
 *
 * @param <T> The instance type.
 * @param <R> The recipe target type, may be Void.
 * @author rubensworks
 */
public final class RecipeComponent<T, R> {

    public static final RecipeComponent<ItemStack, ItemHandlerRecipeTarget>   ITEMSTACK  = new RecipeComponent<>("minecraft:itemstack");
    public static final RecipeComponent<FluidStack, FluidHandlerRecipeTarget> FLUIDSTACK = new RecipeComponent<>("minecraft:fluidstack");
    public static final RecipeComponent<Integer, IEnergyStorage>              ENERGY     = new RecipeComponent<>("minecraft:energy");

    private final ResourceLocation name;

    public RecipeComponent(ResourceLocation name) {
        this.name = name;
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
}
