package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.capability.AllCapability;
import com.til.abyss_mana_2.common.capability.IManaHandle;
import com.til.abyss_mana_2.common.capability.IShapedDrive;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber()
public class BindType extends RegisterBasics<BindType> {

    public static IForgeRegistry<BindType> register = null;

    public BindType(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }

    public BindType(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<BindType> event) {
        event.getRegistry().register(this);
    }

    public static class BundTypeBindCapability<C> extends BindType {

        public final Capability<C> capability;

        public BundTypeBindCapability(String name, Capability<C> capability) {
            this(new ResourceLocation(AbyssMana2.MODID, name), capability);
        }

        public BundTypeBindCapability(ResourceLocation resourceLocation, Capability<C> capability) {
            super(resourceLocation);
            this.capability = capability;
        }

        public Capability<C> getCapability() {
            return capability;
        }
    }

    public static BundTypeBindCapability<IItemHandler> itemIn;
    public static BundTypeBindCapability<IItemHandler> itemOut;
    public static BundTypeBindCapability<IManaHandle> manaIn;
    public static BundTypeBindCapability<IManaHandle> manaOut;
    public static BundTypeBindCapability<IFluidHandler> fluidIn;
    public static BundTypeBindCapability<IFluidHandler> fluidOut;
    public static BundTypeBindCapability<IShapedDrive> modelStore;

    public static BindType relayIn;
    public static BindType relayOut;

    public static void init() {
        itemIn = new BundTypeBindCapability<>("item_in", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        itemOut = new BundTypeBindCapability<>("item_out", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        manaIn = new BundTypeBindCapability<>("mana_in", AllCapability.I_MANA_HANDEL);
        manaOut = new BundTypeBindCapability<>("mana_out", AllCapability.I_MANA_HANDEL);
        fluidIn = new BundTypeBindCapability<>("fluid_in", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        fluidOut = new BundTypeBindCapability<>("fluid_out", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        modelStore = new BundTypeBindCapability<>("model_store", AllCapability.I_SHAPED_DRIVE);
        relayIn = new BindType("relay_in");
        relayOut = new BindType("relay_out");
    }

}
