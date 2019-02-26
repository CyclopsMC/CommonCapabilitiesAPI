package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;
import org.cyclops.commoncapabilities.api.ingredient.IPrototypedIngredient;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;

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
public class PrototypedIngredientAlternativesItemStackOredictionary implements IPrototypedIngredientAlternatives<ItemStack, Integer> {

    public static final PrototypedIngredientAlternativesItemStackOredictionary.Serializer SERIALIZER = new PrototypedIngredientAlternativesItemStackOredictionary.Serializer();
    static {
        SERIALIZERS.put((byte) 1, SERIALIZER);
    }

    private static final LoadingCache<String, List<ItemStack>> CACHE_OREDICT = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, List<ItemStack>>() {
                @Override
                public List<ItemStack> load(String key) throws Exception {
                    return OreDictionary.getOres(key);
                }
            });

    private final List<String> keys;
    private final Integer matchCondition;

    public PrototypedIngredientAlternativesItemStackOredictionary(List<String> keys, Integer matchCondition) {
        this.keys = keys;
        this.matchCondition = matchCondition;
    }

    public Collection<IPrototypedIngredient<ItemStack, Integer>> getAlternatives() {
        return this.keys.stream().flatMap((key) -> {
            try {
                return CACHE_OREDICT.get(key).stream();
            } catch (ExecutionException e) {
                return Stream.empty();
            }
        }).flatMap(itemStack -> ItemStackHelpers.getVariants(itemStack).stream())
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

    @Override
    public String toString() {
        return "[PrototypedIngredientAlternativesList: " + getAlternatives().toString() + "]";
    }

    public static class Serializer implements IPrototypedIngredientAlternatives.ISerializer<PrototypedIngredientAlternativesItemStackOredictionary> {
        @Override
        public byte getId() {
            return 1;
        }

        @Override
        public <T, M> NBTBase serialize(IngredientComponent<T, M> ingredientComponent, PrototypedIngredientAlternativesItemStackOredictionary alternatives) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList keys = new NBTTagList();
            for (String key : alternatives.keys) {
                keys.appendTag(new NBTTagString(key));
            }
            tag.setTag("keys", keys);
            tag.setInteger("match", alternatives.matchCondition);
            return tag;
        }

        @Override
        public <T, M> PrototypedIngredientAlternativesItemStackOredictionary deserialize(IngredientComponent<T, M> ingredientComponent, NBTBase tag) {
            NBTTagCompound tagCompound = (NBTTagCompound) tag;
            if (!tagCompound.hasKey("keys")) {
                throw new IllegalArgumentException("A oredict prototyped alternatives did not contain valid keys");
            }
            if (!tagCompound.hasKey("match")) {
                throw new IllegalArgumentException("A oredict prototyped alternatives did not contain a valid match");
            }
            NBTTagList keysTag = tagCompound.getTagList("keys", Constants.NBT.TAG_STRING);
            List<String> keys = Lists.newArrayList();
            for (NBTBase key : keysTag) {
                keys.add(((NBTTagString) key).getString());
            }
            int matchCondition = tagCompound.getInteger("match");
            return new PrototypedIngredientAlternativesItemStackOredictionary(keys, matchCondition);
        }
    }
}
