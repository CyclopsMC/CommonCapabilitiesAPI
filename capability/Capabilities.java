package org.cyclops.commoncapabilities.api.capability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.cyclops.commoncapabilities.Reference;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import org.cyclops.commoncapabilities.api.capability.temperature.ITemperature;
import org.cyclops.commoncapabilities.api.capability.work.IWorker;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageHandler;

/**
 * @author rubensworks
 */
public class Capabilities {

    public static final class SlotlessItemHandler {
        public static final BlockCapability<ISlotlessItemHandler, Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(Reference.MOD_ID, "slotless_item_handler"), ISlotlessItemHandler.class);
        public static final ItemCapability<ISlotlessItemHandler, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(Reference.MOD_ID, "slotless_item_handler"), ISlotlessItemHandler.class);
        public static final EntityCapability<ISlotlessItemHandler, Void> ENTITY = EntityCapability.createVoid(new ResourceLocation(Reference.MOD_ID, "slotless_item_handler"), ISlotlessItemHandler.class);
    }

    public static final class IngredientComponentStorageHandler {
        public static final BlockCapability<IIngredientComponentStorageHandler, Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(Reference.MOD_ID, "ingredient_component_storage_handler"), IIngredientComponentStorageHandler.class);
        public static final ItemCapability<IIngredientComponentStorageHandler, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(Reference.MOD_ID, "ingredient_component_storage_handler"), IIngredientComponentStorageHandler.class);
        public static final EntityCapability<IIngredientComponentStorageHandler, Direction> ENTITY = EntityCapability.createSided(new ResourceLocation(Reference.MOD_ID, "ingredient_component_storage_handler"), IIngredientComponentStorageHandler.class);
    }

    public static final class Worker {
        public static final BlockCapability<IWorker, Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(Reference.MOD_ID, "worker"), IWorker.class);
        public static final ItemCapability<IWorker, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(Reference.MOD_ID, "worker"), IWorker.class);
        public static final EntityCapability<IWorker, Direction> ENTITY = EntityCapability.createSided(new ResourceLocation(Reference.MOD_ID, "worker"), IWorker.class);
    }

    public static final class Temperature {
        public static final BlockCapability<ITemperature, Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(Reference.MOD_ID, "temperature"), ITemperature.class);
        public static final ItemCapability<ITemperature, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(Reference.MOD_ID, "temperature"), ITemperature.class);
        public static final EntityCapability<ITemperature, Direction> ENTITY = EntityCapability.createSided(new ResourceLocation(Reference.MOD_ID, "temperature"), ITemperature.class);
    }

    public static final class RecipeHandler {
        public static final BlockCapability<IRecipeHandler, Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(Reference.MOD_ID, "recipe_handler"), IRecipeHandler.class);
        public static final ItemCapability<IRecipeHandler, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(Reference.MOD_ID, "recipe_handler"), IRecipeHandler.class);
        public static final EntityCapability<IRecipeHandler, Direction> ENTITY = EntityCapability.createSided(new ResourceLocation(Reference.MOD_ID, "recipe_handler"), IRecipeHandler.class);
    }

    public static final class InventoryState {
        public static final BlockCapability<IInventoryState, Direction> BLOCK = BlockCapability.createSided(new ResourceLocation(Reference.MOD_ID, "inventory_state"), IInventoryState.class);
        public static final ItemCapability<IInventoryState, Void> ITEM = ItemCapability.createVoid(new ResourceLocation(Reference.MOD_ID, "inventory_state"), IInventoryState.class);
        public static final EntityCapability<IInventoryState, Direction> ENTITY = EntityCapability.createSided(new ResourceLocation(Reference.MOD_ID, "inventory_state"), IInventoryState.class);
    }

}
