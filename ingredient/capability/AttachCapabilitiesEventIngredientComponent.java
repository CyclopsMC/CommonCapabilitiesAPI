package org.cyclops.commoncapabilities.api.ingredient.capability;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;

/**
 * Event for when an {@link IngredientComponent} is being constructed that is emitted on the mod event bus.
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class AttachCapabilitiesEventIngredientComponent<T, M> extends AttachCapabilitiesEvent<IngredientComponent<T, M>> implements IModBusEvent {

    public AttachCapabilitiesEventIngredientComponent(IngredientComponent<T, M> ingredientComponent) {
        super((Class<IngredientComponent<T, M>>) (Class) IngredientComponent.class, ingredientComponent);
    }

    public IngredientComponent<T, M> getIngredientComponent() {
        return getObject();
    }
}
