package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IPrototypedIngredient;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                    net.minecraft.tags.Tag<Item> tag = ItemTags.getAllTags().getTag(new ResourceLocation(key));
                    if (tag == null) {
                        return Collections.emptyList();
                    }
                    return tag.getValues();
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
        return obj instanceof IPrototypedIngredientAlternatives
                && this.getAlternatives().equals(((IPrototypedIngredientAlternatives) obj).getAlternatives());
    }

    @Override
    public int hashCode() {
        int inputsHash = 333;
        for (IPrototypedIngredient<ItemStack, Integer> value : getAlternatives()) {
            inputsHash |= value.hashCode();
        }
        return 1235 | inputsHash << 2;
    }

    public long getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "[PrototypedIngredientAlternativesList: " + getAlternatives().toString() + "]";
    }

    public static class Serializer implements IPrototypedIngredientAlternatives.ISerializer<PrototypedIngredientAlternativesItemStackTag> {
        @Override
        public byte getId() {
            return 1;
        }

        @Override
        public <T, M> Tag serialize(IngredientComponent<T, M> ingredientComponent, PrototypedIngredientAlternativesItemStackTag alternatives) {
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
        public <T, M> PrototypedIngredientAlternativesItemStackTag deserialize(IngredientComponent<T, M> ingredientComponent, Tag tag) {
            CompoundTag tagCompound = (CompoundTag) tag;
            if (!tagCompound.contains("keys")) {
                throw new IllegalArgumentException("A oredict prototyped alternatives did not contain valid keys");
            }
            if (!tagCompound.contains("match")) {
                throw new IllegalArgumentException("A oredict prototyped alternatives did not contain a valid match");
            }
            ListTag keysTag = tagCompound.getList("keys", Tag.TAG_STRING);
            List<String> keys = Lists.newArrayList();
            for (Tag key : keysTag) {
                keys.add(key.getAsString());
            }
            int matchCondition = tagCompound.getInt("match");
            long quantity = tagCompound.getLong("quantity");
            return new PrototypedIngredientAlternativesItemStackTag(keys, matchCondition, quantity);
        }
    }
}
