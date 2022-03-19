package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.common.ModTab;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class ManaLevelItem extends RegisterBasics<ManaLevelItem> {

    public static IForgeRegistry<ManaLevelItem> register = null;

    public ManaLevelItem(String name, GenericParadigmMap genericParadigmMap) {
        this(new ResourceLocation(AbyssMana2.MODID, name), genericParadigmMap);
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

    public ManaLevelItem(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        super(resourceLocation, genericParadigmMap);
    }

    /***
     * 控制晶体
     */
    public static ManaLevelItem controlCrystal;

    /***
     * 棱镜
     */
    // public static ManaLevelItem lens;

    public static ManaLevelItem cpu;

    public static ManaLevelItem ram;

    public static ManaLevelItem rom;

    public static ManaLevelItem io;

    public static void init() {
  /*      lens = new ManaLevelItem("lens", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "Lens"));*/
        controlCrystal = new ManaLevelItem("control_crystal", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "ControlCrystal"));
        cpu = new ManaLevelItem("cpu", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "CPU"));
        ram = new ManaLevelItem("ram", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "RAM"));
        rom = new ManaLevelItem("rom", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "ROM"));
        io = new ManaLevelItem("io", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "IO"));
    }
}
