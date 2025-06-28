package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
                                    .getTagOrEmpty(TagKey.create(Registries.ITEM, ResourceLocation.parse(key)))
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
        public <T, M> Tag serialize(HolderLookup.Provider lookupProvider, IngredientComponent<T, M> ingredientComponent, PrototypedIngredientAlternativesItemStackTag alternatives) {
            CompoundTag tag = new CompoundTag();
            ListTag keys = new ListTag();
            for (String key : alternatives.keys) {
                keys.add(StringTag.valueOf(key));
            }
            tag.put("keys", keys);
            tag.putInt("match", alternatives.matchCondition);
            tag.putLong("quantity", alternatives.quantity);
            return tag;
        }

        @Override
        public <T, M> PrototypedIngredientAlternativesItemStackTag deserialize(HolderLookup.Provider lookupProvider, IngredientComponent<T, M> ingredientComponent, Tag tag) {
            CompoundTag tagCompound = (CompoundTag) tag;
            ListTag keysTag = tagCompound.getList("keys").orElseThrow(() -> new IllegalArgumentException("A oredict prototyped alternatives did not contain valid keys"));
            List<String> keys = Lists.newArrayList();
            for (Tag key : keysTag) {
                keys.add(key.asString().orElseThrow());
            }
            int matchCondition = tagCompound.getInt("match")
                    .orElseThrow(() -> new IllegalArgumentException("A oredict prototyped alternatives did not contain a valid match"));
            long quantity = tagCompound.getLongOr("quantity", 1);
            return new PrototypedIngredientAlternativesItemStackTag(keys, matchCondition, quantity);
        }
    }
}
