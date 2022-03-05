package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.ModTab;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class ManaLevelItem extends RegisterBasics<ManaLevelItem> {

    public static IForgeRegistry<ManaLevelItem> register = null;

    public ManaLevelItem(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }

    public Item getItem(ManaLevel manaLevel) {
        String name = Objects.requireNonNull(manaLevel.getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(getRegistryName()).getResourcePath();
        return new Item()
                .setRegistryName(new ResourceLocation(AbyssMana2.MODID, name))
                .setUnlocalizedName(AbyssMana2.MODID + "." + name)
                .setMaxStackSize(64)
                .setCreativeTab(ModTab.TAB);
    }

    public int getLayer() {
        return 0;
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<ManaLevelItem> event) {
        event.getRegistry().register(this);
    }

    public ManaLevelItem(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }


    public static void init() {

    }
}
