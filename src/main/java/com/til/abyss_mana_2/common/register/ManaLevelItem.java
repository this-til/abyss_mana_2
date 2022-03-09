package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.common.ModTab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        ManaLevelItem manaLevelItem = this;
        return new Item() {
            @Override
            public String getItemStackDisplayName(ItemStack stack) {
                return Lang.getLang(manaLevel, manaLevelItem);
            }
        }
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

    /***
     * 控制晶体
     */
    public static ManaLevelItem controlCrystal;

    /***
     * 棱镜
     */
    public static ManaLevelItem lens;

    public static ManaLevelItem cpu;

    public static ManaLevelItem ram;

    public static ManaLevelItem rom;

    public static ManaLevelItem io;

    public static void init() {
        lens = (ManaLevelItem) new ManaLevelItem("lens").setOrePrefix("Lens");
        controlCrystal = (ManaLevelItem) new ManaLevelItem("control_crystal").setOrePrefix("ControlCrystal");
        cpu = (ManaLevelItem) new ManaLevelItem("cpu").setOrePrefix("CPU");
        ram = (ManaLevelItem) new ManaLevelItem("ram").setOrePrefix("RAM");
        rom = (ManaLevelItem) new ManaLevelItem("rom").setOrePrefix("ROM");
        io = (ManaLevelItem) new ManaLevelItem("io").setOrePrefix("IO");
    }
}
