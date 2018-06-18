package org.cyclops.commoncapabilities.api.ingredient.capability;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;

import java.util.Map;

/**
 * A helper manager that can be used to easily attach capabilities to ingredient components.
 * This manager must be instantiated during mod initialization,
 * and instances of {@link IIngredientComponentCapabilityAttacher} can be registered to it.
 *
 * When the ingredient components are being registered later on,
 * this manager will ensure that the registered attachers are called,
 * and that their capabilities are attached.
 *
 * @author rubensworks
 */
public class IngredientComponentCapabilityAttacherManager {

    private final Map<ResourceLocation, IIngredientComponentCapabilityAttacher<?, ?>> attachers;

    public IngredientComponentCapabilityAttacherManager() {
        this.attachers = Maps.newHashMap();
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Register the given capability attacher.
     * @param attacher A capability attacher that will be called when the corresponding capability is instantiated.
     */
    public void addAttacher(IIngredientComponentCapabilityAttacher<?, ?> attacher) {
        this.attachers.put(attacher.getTargetName(), attacher);
    }

    @SubscribeEvent
    public void onIngredientComponentsLoad(AttachCapabilitiesEventIngredientComponent event) {
        onIngredientComponentLoad(event, event.getIngredientComponent());

    }

    protected <T, M> void onIngredientComponentLoad(AttachCapabilitiesEventIngredientComponent event,
                                                    IngredientComponent<T, M> ingredientComponent) {
        IIngredientComponentCapabilityAttacher<T, M> attacher = (IIngredientComponentCapabilityAttacher<T, M>) attachers.get(ingredientComponent.getName());
        if (attacher != null) {
            event.addCapability(attacher.getCapabilityProviderName(),
                    attacher.createCapabilityProvider(ingredientComponent));
        }
    }

}
