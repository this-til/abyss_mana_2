package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.common.ModTab;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Detainted;
import java.util.Objects;

public class OreType extends RegisterBasics<OreType> {

    public static IForgeRegistry<OreType> register = null;

    public OreType(String name, GenericParadigmMap genericParadigmMap) {
        this(new ResourceLocation(AbyssMana2.MODID, name), genericParadigmMap);
    }


    public OreType(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        super(resourceLocation, genericParadigmMap);
    }

    public Item getItem(Ore ore) {
        OreType oreType = this;
        String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
        return new Item() {
            @Override
            public String getItemStackDisplayName(ItemStack stack) {
                return Lang.getLang(ore, oreType);
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
    public void register(RegistryEvent.Register<OreType> event) {
        event.getRegistry().register(this);
    }

    /***
     * 矿物锭
     */
    public static OreType ingot;

    /***
     * 浸润魔力锭
     */
    public static OreType infiltrationIngot;

    /***
     * 粒
     */
    public static OreType nuggets;

    /***
     * 粉碎矿物
     */
    public static OreType crushed;

    /***
     * 纯净矿物
     * 由粉碎矿物洗涤而得到
     */
    public static OreType crushedPurified;

    /***
     * 矿粉
     */
    public static OreType dust;

    /***
     * 浸润魔力粉
     */
    public static OreType infiltrationDust;

    /***
     * 小撮粉
     */
    public static OreType dustTiny;

    /***
     * 升华状态
     */
    public static OreType sublimation;

    /***
     * 晶体状态
     * 由升华状态结晶成，研磨成粉
     */
    public static OreType crystal;

    /***
     * 透镜
     */
    @Detainted
    public static OreType lens;

    /***
     * 线
     */
    public static OreType string;

    /***
     * 纸
     */
    public static OreType paper;

    public static void init() {
        ingot = new OreType("ingot", new GenericParadigmMap().put_genericParadigm(orePrefix, "ingot")) {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 粒 -> 锭
                    registerOreRecipe(getRecipeNameOfAToB(nuggets, ore, this, ore), new ItemStack(ore.item.get(this)),
                            "AAA", "AAA", "AAA", 'A', getOreString(nuggets, ore));
                }
            }

            @SubscribeEvent
            public void _onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 块 -> 锭
                    registerOreRecipe(getRecipeNameOfAToB(OreBlock.block, ore, this, ore), new ItemStack(ore.item.get(this), 9),
                            "A  ", "   ", "   ", 'A', getOreString(OreBlock.block, ore));
                }
            }

        };
        infiltrationIngot = new OreType("infiltration_ingot", new GenericParadigmMap().put_genericParadigm(orePrefix, "infiltrationIngot"));
        nuggets = new OreType("nuggets", new GenericParadigmMap().put_genericParadigm(orePrefix, "nuggets")) {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 锭 -> 粒
                    registerOreRecipe(getRecipeNameOfAToB(ingot, ore, this, ore), new ItemStack(ore.item.get(this), 9),
                            "A  ", "   ", "   ", 'A', getOreString(ingot, ore));
                }
            }
        };
        crushedPurified = new OreType("crushed_purified", new GenericParadigmMap().put_genericParadigm(orePrefix, "crushedPurified"));
        crushed = new OreType("crushed", new GenericParadigmMap().put_genericParadigm(orePrefix, "crushed"));
        dust = new OreType("dust", new GenericParadigmMap().put_genericParadigm(orePrefix, "dust")) {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 小粉 -> 粉
                    registerOreRecipe(getRecipeNameOfAToB(dustTiny, ore, this, ore), new ItemStack(ore.item.get(this)),
                            "AAA", "AAA", "AAA", 'A', getOreString(dustTiny, ore));
                }
            }
        };
        infiltrationDust = new OreType("infiltration_dust", new GenericParadigmMap().put_genericParadigm(orePrefix, "infiltrationDust"));
        dustTiny = new OreType("dust_tiny", new GenericParadigmMap().put_genericParadigm(orePrefix, "dustTiny")) {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 粉 -> 小粉
                    registerOreRecipe(getRecipeNameOfAToB(dust, ore, this, ore), new ItemStack(ore.item.get(this), 9),
                            "A  ", "   ", "   ", 'A', getOreString(dust, ore));
                }
            }
        };
        sublimation = new OreType("sublimation", new GenericParadigmMap().put_genericParadigm(orePrefix, "sublimation"));
        crystal = new OreType("crystal", new GenericParadigmMap().put_genericParadigm(orePrefix, "crystal"));
        // lens = new OreType("lens").setOreType("lens");
        string = new OreType("string", new GenericParadigmMap().put_genericParadigm(orePrefix, "string"));
        paper = new OreType("paper", new GenericParadigmMap().put_genericParadigm(orePrefix, "paper"));
    }
}
