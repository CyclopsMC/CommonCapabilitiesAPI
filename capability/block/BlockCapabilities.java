package org.cyclops.commoncapabilities.api.capability.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * The general registry for capabilities of blocks.
 * This is used to register capabilities to blocks AND to lookup capabilities of blocks.
 * @author rubensworks
 */
public class BlockCapabilities implements IBlockCapabilityProvider {

    private List<IBlockCapabilityConstructor> capabilityConstructors = Lists.newLinkedList();

    private final Map<Block, IBlockCapabilityProvider[]> providers = Maps.newIdentityHashMap();

    private static final BlockCapabilities INSTANCE = new BlockCapabilities();

    private BlockCapabilities() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    public static BlockCapabilities getInstance() {
        return INSTANCE;
    }

    /**
     * Register a capability provider for the given block or for all blocks.
     * This MUST be called after the given block is registered, otherwise the block instance may be null.
     *
     * @param block The block the capability provider applies to,
     *              null if it applies to all blocks.
     *              Only use null if absolutely necessary, as this will reduce performance during lookup.
     * @param capabilityProvider The capability provider
     */
    protected void register(@Nullable Block block, @Nonnull IBlockCapabilityProvider capabilityProvider) {
        IBlockCapabilityProvider[] providers = this.providers.get(block);
        if (providers == null) {
            providers = new IBlockCapabilityProvider[0];
        }
        providers = ArrayUtils.add(providers, capabilityProvider);
        this.providers.put(block, providers);
    }

    /**
     * Register a block capability provider constructor.
     * This will make sure that the constructor is only called AFTER all blocks have been registered.
     * So this method can be called at any time.
     *
     * @param capabilityConstructor A constructor for a block capability provider.
     */
    public void register(@Nonnull IBlockCapabilityConstructor capabilityConstructor) {
        if (ForgeRegistries.BLOCKS.getKeys().isEmpty() && this.capabilityConstructors != null) {
            this.capabilityConstructors.add(capabilityConstructor);
        } else {
            register(capabilityConstructor.getBlock(), capabilityConstructor.createProvider());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void afterBlocksRegistered(RegistryEvent.Register<Block> event) {
        for (IBlockCapabilityConstructor capabilityConstructor : this.capabilityConstructors) {
            register(capabilityConstructor.getBlock(), capabilityConstructor.createProvider());
        }
        this.capabilityConstructors = null;
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull BlockState blockState, @Nonnull Capability<T> capability,
                                          @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction facing) {
        IBlockCapabilityProvider[] providers = this.providers.get(blockState.getBlock());
        if (providers != null) {
            for (IBlockCapabilityProvider provider : providers) {
                LazyOptional<T> capabilityHolder = provider.getCapability(blockState, capability, world, pos, facing);
                if (capabilityHolder.isPresent()) {
                    return capabilityHolder;
                }
            }
        } else {
            providers = this.providers.get(null);
            if (providers != null) {
                for (IBlockCapabilityProvider provider : providers) {
                    LazyOptional<T> capabilityHolder = provider.getCapability(blockState, capability, world, pos, facing);
                    if (capabilityHolder.isPresent()) {
                        return capabilityHolder;
                    }
                }

            }
        }
        return null;
    }

}
