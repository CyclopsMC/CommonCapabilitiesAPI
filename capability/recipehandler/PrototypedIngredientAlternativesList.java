package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientSerializer;
import org.cyclops.commoncapabilities.api.ingredient.IPrototypedIngredient;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;

import java.util.Collection;
import java.util.List;

/**
 * A list-based {@link IPrototypedIngredientAlternatives} implementation.
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void.
 * @author rubensworks
 */
public class PrototypedIngredientAlternativesList<T, M> implements IPrototypedIngredientAlternatives<T, M> {

    public static final PrototypedIngredientAlternativesList.Serializer SERIALIZER = new PrototypedIngredientAlternativesList.Serializer();

    private final List<IPrototypedIngredient<T, M>> alternatives;

    public PrototypedIngredientAlternativesList(List<IPrototypedIngredient<T, M>> alternatives) {
        this.alternatives = alternatives;
    }

    public Collection<IPrototypedIngredient<T, M>> getAlternatives() {
        return this.alternatives;
    }

    @Override
    public ISerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IPrototypedIngredientAlternatives
                && this.getAlternatives().equals(((IPrototypedIngredientAlternatives) obj).getAlternatives());
    }

    @Override
    public int hashCode() {
        int inputsHash = 333;
        for (IPrototypedIngredient<T, M> value : getAlternatives()) {
            inputsHash |= value.hashCode();
        }
        return 1235 | inputsHash << 2;
    }

    @Override
    public String toString() {
        return "[PrototypedIngredientAlternativesList: " + alternatives.toString() + "]";
    }

    public static class Serializer implements IPrototypedIngredientAlternatives.ISerializer<PrototypedIngredientAlternativesList<?, ?>> {
        @Override
        public byte getId() {
            return 0;
        }

        @Override
        public <T, M> void serialize(ValueOutput valueOutput, IngredientComponent<T, M> ingredientComponent, PrototypedIngredientAlternativesList<?, ?> alternatives) {
            ValueOutput.ValueOutputList prototypes = valueOutput.childrenList("l");
            IIngredientSerializer serializer = ingredientComponent.getSerializer();
            for (IPrototypedIngredient prototypedIngredient : (List<IPrototypedIngredient>) (List) alternatives.alternatives) {
                ValueOutput prototypeTag = prototypes.addChild();
                serializer.serializeInstance(prototypeTag.child("prototype"), prototypedIngredient.getPrototype());
                prototypeTag.store("condition", ExtraCodecs.NBT, serializer.serializeCondition(prototypedIngredient.getCondition()));
            }
        }

        @Override
        public <T, M> PrototypedIngredientAlternativesList<?, ?> deserialize(ValueInput valueInput, IngredientComponent<T, M> ingredientComponent) {
            ValueInput.ValueInputList instancesTag = valueInput.childrenList("l").orElseThrow();
            List<IPrototypedIngredient<T, M>> instances = Lists.newArrayList();
            IIngredientSerializer<T, M> serializer = ingredientComponent.getSerializer();
            for (ValueInput prototypeTag : instancesTag) {
                instances.add(new PrototypedIngredient<>(ingredientComponent,
                        serializer.deserializeInstance(prototypeTag.child("prototype").orElseThrow()),
                        serializer.deserializeCondition(prototypeTag.read("condition", ExtraCodecs.NBT).orElseThrow())));
            }
            return new PrototypedIngredientAlternativesList<>(instances);
        }
    }

}
