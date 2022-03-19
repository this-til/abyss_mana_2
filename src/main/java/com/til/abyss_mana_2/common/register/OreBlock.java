package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.AllBlock;
import com.til.abyss_mana_2.common.ModTab;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class OreBlock extends RegisterBasics<OreBlock> {

    public static IForgeRegistry<OreBlock> register = null;

    public OreBlock(String name, GenericParadigmMap genericParadigmMap) {
        this(new ResourceLocation(AbyssMana2.MODID, name), genericParadigmMap);
    }

    public OreBlock(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        super(resourceLocation, genericParadigmMap);
    }

    public Block getBlock(Ore ore) {
        String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
        return new AllBlock.ModBlock(Material.ROCK, MapColor.BLACK, SoundType.STONE, ModTab.TAB, new ResourceLocation(AbyssMana2.MODID, name), "pickaxe", 2, 2.25f, 12);
    }

    public int getLayer() {
        return 1;
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<OreBlock> event) {
        event.getRegistry().register(this);
    }

    public static class OreMineral extends OreBlock {
        public OreMineral(String name, GenericParadigmMap genericParadigmMap) {
            super(name, genericParadigmMap);
        }

        public OreMineral(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
            super(resourceLocation, genericParadigmMap);
        }
    }

    public static OreMineral lordWorld;
    public static OreMineral lordWorldSand;
    public static OreMineral lordWorldDirt;
    public static OreMineral lordWorldGravel;
    public static OreMineral netherWorldNetherrack;
    public static OreMineral endWorldEndStone;

    public static OreBlock block;

    /***
     * 支架
     */
    public static OreBlock bracket;

    public static void init() {
        lordWorld = new OreMineral("lord_world", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "ore"));
        lordWorldSand = new OreMineral("lord_world_sand", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "ore")) {
            @Override
            public Block getBlock(Ore ore) {
                String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
                return new AllBlock.FallBlockBasic(Material.GROUND, SoundType.GROUND, ModTab.TAB, new ResourceLocation(AbyssMana2.MODID, name), "shovel", 2, 2.25f, 12);

            }
        };
        lordWorldDirt = new OreMineral("lord_world_dirt", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "ore")) {
            @Override
            public Block getBlock(Ore ore) {
                String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
                return new AllBlock.ModBlock(Material.ROCK, MapColor.BLACK, SoundType.STONE, ModTab.TAB, new ResourceLocation(AbyssMana2.MODID, name), "shovel", 2, 2.25f, 12);
            }
        };
        lordWorldGravel = new OreMineral("lord_world_gravel", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "ore")) {
            @Override
            public Block getBlock(Ore ore) {
                String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
                return new AllBlock.ModBlock(Material.GROUND, MapColor.BLACK, SoundType.GROUND, ModTab.TAB, new ResourceLocation(AbyssMana2.MODID, name), "shovel", 2, 2.25f, 12);
            }
        };
        netherWorldNetherrack = new OreMineral("nether_world_netherrack", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "ore"));
        endWorldEndStone = new OreMineral("end_world_end_stone", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "ore"));

        block = new OreBlock("block", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "block")) {
            @Override
            public int getLayer() {
                return 0;
            }

            // 锭 -> 块
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    registerOreRecipe(getRecipeNameOfAToB(OreType.ingot, ore, this, ore), new ItemStack(ore.block.get(this)),
                            "AAA", "AAA", "AAA", 'A', getOreString(OreType.ingot, ore));
                }
            }

        };
        bracket = new OreBlock("bracket", new GenericParadigmMap()
                .put_genericParadigm(orePrefix, "bracket")) {
            @Override
            public int getLayer() {
                return 0;
            }

            @Override
            public Block getBlock(Ore ore) {
                return new AllBlock.TranslucentBlock(Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath());
            }
        };
    }

}
