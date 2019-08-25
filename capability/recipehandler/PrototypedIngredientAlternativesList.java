package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
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
        public <T, M> INBT serialize(IngredientComponent<T, M> ingredientComponent, PrototypedIngredientAlternativesList<?, ?> alternatives) {
            ListNBT prototypes = new ListNBT();
            IIngredientSerializer serializer = ingredientComponent.getSerializer();
            for (IPrototypedIngredient prototypedIngredient : (List<IPrototypedIngredient>) (List) alternatives.alternatives) {
                CompoundNBT prototypeTag = new CompoundNBT();
                prototypeTag.put("prototype", serializer.serializeInstance(prototypedIngredient.getPrototype()));
                prototypeTag.put("condition", serializer.serializeCondition(prototypedIngredient.getCondition()));
                prototypes.add(prototypeTag);
            }
            return prototypes;
        }

        @Override
        public <T, M> PrototypedIngredientAlternativesList<?, ?> deserialize(IngredientComponent<T, M> ingredientComponent, INBT tag) {
            String componentName = ingredientComponent.getName().toString();
            ListNBT instancesTag = (ListNBT) tag;
            List<IPrototypedIngredient<T, M>> instances = Lists.newArrayList();
            IIngredientSerializer<T, M> serializer = ingredientComponent.getSerializer();
            for (INBT prototypeTag : instancesTag) {
                if (!(prototypeTag instanceof CompoundNBT)) {
                    throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid sublist with NBTTagCompunds");
                }
                CompoundNBT safePrototypeTag = (CompoundNBT) prototypeTag;
                if (!safePrototypeTag.contains("prototype")) {
                    throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid sublist with a prototype entry");
                }
                if (!safePrototypeTag.contains("condition")) {
                    throw new IllegalArgumentException("The ingredient component type " + componentName + " did not contain a valid sublist with a condition entry");
                }
                instances.add(new PrototypedIngredient<>(ingredientComponent,
                        serializer.deserializeInstance(safePrototypeTag.get("prototype")),
                        serializer.deserializeCondition(safePrototypeTag.get("condition"))));
            }
            return new PrototypedIngredientAlternativesList<>(instances);
        }
    }

}
