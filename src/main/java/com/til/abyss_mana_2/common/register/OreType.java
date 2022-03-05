package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.common.ModTab;
import com.til.abyss_mana_2.common.event.ModEvent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class OreType extends RegisterBasics<OreType> {

    public static IForgeRegistry<OreType> register = null;

    /***
     * 保留大写
     */
    protected String orePrefix;

    public OreType(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }


    public OreType(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    /***
     * 获取ore前缀
     */
    public String getOrePrefix() {
        if (orePrefix.isEmpty()) {
            ResourceLocation resourceLocation = getRegistryName();
            if (resourceLocation != null) {
                orePrefix = resourceLocation.getResourcePath();
                return orePrefix;
            }
        }
        return orePrefix;
    }

    public OreType setOreType(String orePrefix) {
        this.orePrefix = orePrefix;
        return this;
    }

    public Item getItem(Ore ore) {
        OreType oreType = this;
        String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
        return new Item(){

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
    public static OreType lens;

    public static void init() {
        ingot = new OreType("ingot") {
           /* @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 粒 -> 锭
                    registerOreRecipe(getRecipeNameOfAToB(nuggets, ore, this, ore), new ItemStack(ore.item.get(this)),
                            "AAA", "AAA", "AAA", 'A', getOreString(nuggets, ore));
                }
            }*/

        /*    @SubscribeEvent
            public void _onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 块 -> 锭
                    registerOreRecipe(getRecipeNameOfAToB(OreBlock.block, ore, this, ore), new ItemStack(ore.item.get(this), 9),
                            "A  ", "   ", "   ", 'A', getOreString(OreBlock.block, ore));
                }
            }*/

        }.setOreType("ingot");
        infiltrationIngot = new OreType("infiltration_ingot").setOreType("infiltrationIngot");
        nuggets = new OreType("nuggets") {
        /*    @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 锭 -> 粒
                    registerOreRecipe(getRecipeNameOfAToB(ingot, ore, this, ore), new ItemStack(ore.item.get(this), 9),
                            "A  ", "   ", "   ", 'A', getOreString(ingot, ore));
                }
            }*/
        }.setOreType("nuggets");
        crushedPurified = new OreType("crushed_purified").setOreType("crushedPurified");
        crushed = new OreType("crushed").setOreType("crushed");
        dust = new OreType("dust") {
           /* @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 小粉 -> 粉
                    registerOreRecipe(getRecipeNameOfAToB(dustTiny, ore, this, ore), new ItemStack(ore.item.get(this)),
                            "AAA", "AAA", "AAA", 'A', getOreString(dustTiny, ore));
                }
            }*/
        }.setOreType("dust");
        infiltrationDust = new OreType("infiltration_dust").setOreType("infiltrationDust");
        dustTiny = new OreType("dust_tiny") {
          /*  @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 粉 -> 小粉
                    registerOreRecipe(getRecipeNameOfAToB(dust, ore, this, ore), new ItemStack(ore.item.get(this), 9),
                            "A  ", "   ", "   ", 'A', getOreString(dust, ore));
                }
            }*/
        }.setOreType("dustTiny");
        sublimation = new OreType("sublimation").setOreType("sublimation");
        crystal = new OreType("crystal").setOreType("crystal");
        lens = new OreType("lens").setOreType("lens");
    }
}
