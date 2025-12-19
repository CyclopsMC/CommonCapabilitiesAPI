package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IPrototypedIngredient;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An oredictionary-based {@link IPrototypedIngredientAlternatives} implementation.
 * @author rubensworks
 */
public class PrototypedIngredientAlternativesItemStackTag implements IPrototypedIngredientAlternatives<ItemStack, Integer> {

    public static final PrototypedIngredientAlternativesItemStackTag.Serializer SERIALIZER = new PrototypedIngredientAlternativesItemStackTag.Serializer();
    static {
        SERIALIZERS.put((byte) 1, SERIALIZER);
    }

    private static final LoadingCache<String, Collection<Item>> CACHE_OREDICT = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, Collection<Item>>() {
                @Override
                public Collection<Item> load(String key) {
                    return StreamSupport.stream(
                            BuiltInRegistries.ITEM
                                    .getTagOrEmpty(TagKey.create(Registries.ITEM, Identifier.parse(key)))
                                    .spliterator(),
                                    false)
                            .map(Holder::value).collect(Collectors.toList());
                }
            });

    private final List<String> keys;
    private final Integer matchCondition;
    private final long quantity;

    public PrototypedIngredientAlternativesItemStackTag(List<String> keys, Integer matchCondition, long quantity) {
        this.keys = keys;
        this.matchCondition = matchCondition;
        this.quantity = quantity;
    }

    public Collection<IPrototypedIngredient<ItemStack, Integer>> getAlternatives() {
        IIngredientMatcher<ItemStack, Integer> matcher = IngredientComponent.ITEMSTACK.getMatcher();
        return this.keys.stream().flatMap((key) -> {
            try {
                return CACHE_OREDICT.get(key).stream();
            } catch (ExecutionException e) {
                return Stream.empty();
            }
        })
                .map(item -> matcher.withQuantity(new ItemStack(item), getQuantity()))
                .map(itemStack -> new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, itemStack, this.matchCondition))
                .collect(Collectors.toList());
    }

    @Override
    public ISerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PrototypedIngredientAlternativesItemStackTag
                && this.keys.equals(((PrototypedIngredientAlternativesItemStackTag) obj).keys)
                && Objects.equals(this.matchCondition, ((PrototypedIngredientAlternativesItemStackTag) obj).matchCondition)
                && Objects.equals(this.quantity, ((PrototypedIngredientAlternativesItemStackTag) obj).quantity);
    }

    @Override
    public int hashCode() {
        return 1235 | this.keys.hashCode() << 2 | matchCondition | (int) quantity;
    }

    public List<String> getKeys() {
        return keys;
    }

    public Integer getMatchCondition() {
        return matchCondition;
    }

    public long getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "[PrototypedIngredientAlternativesList: " + this.keys.toString() + "]";
    }

    public static class Serializer implements IPrototypedIngredientAlternatives.ISerializer<PrototypedIngredientAlternativesItemStackTag> {

        @Override
        public byte getId() {
            return 1;
        }

        @Override
        public <T, M> void serialize(ValueOutput valueOutput, IngredientComponent<T, M> ingredientComponent, PrototypedIngredientAlternativesItemStackTag alternatives) {
            ValueOutput.TypedOutputList<String> keys = valueOutput.list("keys", Codec.STRING);
            for (String key : alternatives.keys) {
                keys.add(key);
            }
            valueOutput.putInt("match", alternatives.matchCondition);
            valueOutput.putLong("quantity", alternatives.quantity);
        }

        @Override
        public <T, M> PrototypedIngredientAlternativesItemStackTag deserialize(ValueInput valueInput, IngredientComponent<T, M> ingredientComponent) {
            ValueInput.TypedInputList<String> keysTag = valueInput.list("keys", Codec.STRING).orElseThrow(() -> new IllegalArgumentException("A oredict prototyped alternatives did not contain valid keys"));
            List<String> keys = Lists.newArrayList(keysTag);
            int matchCondition = valueInput.getInt("match")
                    .orElseThrow(() -> new IllegalArgumentException("A oredict prototyped alternatives did not contain a valid match"));
            long quantity = valueInput.getLongOr("quantity", 1);
            return new PrototypedIngredientAlternativesItemStackTag(keys, matchCondition, quantity);
        }
    }
}
