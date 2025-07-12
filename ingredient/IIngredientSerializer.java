package org.cyclops.commoncapabilities.api.ingredient;

import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * An serializer for instances and condition parameters of a certain component type.
 * @param <T> The instance type to match.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public interface IIngredientSerializer<T, M> {

    /**
     * Serialize an instance to NBT.
     *
     * @param valueOutput The value output.
     * @param instance    An instance.
     */
    public void serializeInstance(ValueOutput valueOutput, T instance);

    /**
     * Deserialize an instance from NBT.
     *
     * @param valueInput The value input.
     * @return An instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given instance.
     */
    public T deserializeInstance(ValueInput valueInput) throws IllegalArgumentException;

    /**
     * Serialize a match condition to NBT.
     * @param matchCondition A match condition.
     * @return An NBT tag.
     */
    public Tag serializeCondition(M matchCondition);

    /**
     * Deserialize a match condition from NBT.
     * @param tag An NBT tag.
     * @return A match condition.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given match condition.
     */
    public M deserializeCondition(Tag tag) throws IllegalArgumentException;

}
