package org.cyclops.commoncapabilities.api.ingredient.capability;

import net.minecraft.resources.Identifier;

/**
 * A base implementation of {@link IIngredientComponentCapabilityAttacher}.
 * @author rubensworks
 */
public abstract class IngredientComponentCapabilityAttacherAdapter<T, M> implements IIngredientComponentCapabilityAttacher<T, M> {

    private final Identifier targetName;
    private final IngredientComponentCapability<?, ?> capability;

    public IngredientComponentCapabilityAttacherAdapter(Identifier targetName, IngredientComponentCapability<?, ?> capability) {
        this.targetName = targetName;
        this.capability = capability;
    }

    @Override
    public Identifier getTargetName() {
        return this.targetName;
    }

    @Override
    public IngredientComponentCapability<?, ?> getCapability() {
        return this.capability;
    }
}
