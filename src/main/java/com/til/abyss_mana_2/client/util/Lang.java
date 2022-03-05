package com.til.abyss_mana_2.client.util;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.register.Ore;
import com.til.abyss_mana_2.common.register.OreBlock;
import com.til.abyss_mana_2.common.register.OreType;
import com.til.abyss_mana_2.common.register.RegisterBasics;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class Lang {

    public static String getLang(Ore ore, OreType oreType) {
        return (I18n.format(Objects.requireNonNull(ore.getRegistryName()).getResourceDomain() + "." + ore.getRegistryName().getResourcePath() + ".name") +
                I18n.format(Objects.requireNonNull(oreType.getRegistryName()).getResourceDomain() + "." + oreType.getRegistryName().getResourcePath() + ".name")).trim();
    }

    public static String getLang(Ore ore, OreBlock oreBlock) {
        return (I18n.format(Objects.requireNonNull(ore.getRegistryName()).getResourceDomain() + "." + ore.getRegistryName().getResourcePath() + ".name") +
                I18n.format(Objects.requireNonNull(oreBlock.getRegistryName()).getResourceDomain() + "." + oreBlock.getRegistryName().getResourcePath() + ".name")).trim();
    }

    public static String getOreBlockLang(Ore ore) {
        return (I18n.format(Objects.requireNonNull(ore.getRegistryName()).getResourceDomain() + "." + ore.getRegistryName().getResourcePath() + ".name") +
                I18n.format(AbyssMana2.MODID + ".ore.name")).trim();
    }

    public static String getLang(RegisterBasics<?> registerBasics) {
        return I18n.format(Objects.requireNonNull(registerBasics.getRegistryName()).getResourceDomain() + "." + registerBasics.getRegistryName().getResourcePath() + ".name");
    }

    public static String getLang(String tile) {
        return I18n.format(AbyssMana2.MODID + "." + tile + ".name");
    }

    public static String getLang(NBTTagString nbtTagString) {
        ResourceLocation resourceLocation = new ResourceLocation(nbtTagString.getString());
        return I18n.format(resourceLocation.getResourceDomain() + "." + resourceLocation.getResourcePath() + ".name");
    }

}
