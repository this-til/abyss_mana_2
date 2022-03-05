package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;

import java.awt.*;
import java.util.Objects;

public class ManaLevel extends RegisterBasics<ManaLevel> {

    public static IForgeRegistry<ManaLevel> register = null;

    public final ManaLevelData manaLevelData;

    public final Map<ManaLevelItem, Item> item = new Map<>();
    public final Map<ManaLevelBlock, Item> itemBlock = new Map<>();
    public final Map<ManaLevelBlock, Block> block = new Map<>();

    public ManaLevel(String name, int maxHandle, int maxParallel, int maxBind, int clockTime, int maxManaContainer, Color color) {
        this(new ResourceLocation(AbyssMana2.MODID, name), new ManaLevelData(maxHandle, maxParallel, maxBind, clockTime, maxManaContainer, color));
    }

    public ManaLevel(ResourceLocation resourceLocation, ManaLevelData manaLevelData) {
        super(resourceLocation);
        this.manaLevelData = manaLevelData;
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<ManaLevel> event) {
        event.getRegistry().register(this);

        for (ManaLevelItem manaLevelItem : ManaLevelItem.register) {
            Item item = manaLevelItem.getItem(this);
            this.item.put(manaLevelItem, item);
            GameData.register_impl(item);
        }

        for (ManaLevelBlock manaLevelBlock : ManaLevelBlock.register) {
            Block block = manaLevelBlock.getBlock(this);
            ItemBlock itemBlock = (ItemBlock) new ItemBlock(block)
                    .setRegistryName(Objects.requireNonNull(block.getRegistryName()))
                    .setUnlocalizedName(AbyssMana2.MODID + "." + block.getRegistryName().getResourcePath());
            this.itemBlock.put(manaLevelBlock, itemBlock);
            this.block.put(manaLevelBlock, block);
            GameData.register_impl(block);
            GameData.register_impl(itemBlock);
        }

    }

    public static ManaLevel getLevel(Item item) throws NoValue {
        for (ManaLevel manaLevel : register) {
            for (java.util.Map.Entry<ManaLevelItem, Item> manaLevelItemItemEntry : manaLevel.item.entrySet()) {
                if (manaLevelItemItemEntry.getValue().equals(item)) {
                    return manaLevel;
                }
            }
            for (java.util.Map.Entry<ManaLevelBlock, Item> manaLevelBlockItemEntry : manaLevel.itemBlock.entrySet()) {
                if (manaLevelBlockItemEntry.getValue().equals(item)) {
                    return manaLevel;
                }
            }
        }
        return ManaLevel.T1;
    }

    public static ManaLevel getLevel(Block block) throws NoValue {
        for (ManaLevel manaLevel : register) {
            for (java.util.Map.Entry<ManaLevelBlock, Block> manaLevelBlockBlockEntry : manaLevel.block.entrySet()) {
                if (manaLevelBlockBlockEntry.getValue().equals(block)) {
                    return manaLevel;
                }
            }
        }
        return ManaLevel.T1;
    }

    public static class ManaLevelData {

        /***
         * 最大配方处理等级
         */
        protected int level;

        /***
         * 最大并行
         */
        protected int maxParallel;

        /***
         * 最大绑定量
         */
        protected int maxBind;

        /***
         * 时钟间隔
         */
        protected int clockTime;

        /***
         * 基础最大灵气容量
         */
        protected int maxManaContainer;

        protected Color color;

        public ManaLevelData() {
        }

        public ManaLevelData(int maxHandle, int maxParallel, int maxBind, int clockTime, int maxManaContainer, Color color) {
            this.level = maxHandle;
            this.maxParallel = maxParallel;
            this.maxBind = maxBind;
            this.clockTime = clockTime;
            this.maxManaContainer = maxManaContainer;
            this.color = color;
        }

        public int getLevel() {
            return level;
        }

        public ManaLevelData setLevel(int level) {
            this.level = level;
            return this;
        }

        public int getMaxParallel() {
            return maxParallel;
        }

        public ManaLevelData setMaxParallel(int maxParallel) {
            this.maxParallel = maxParallel;
            return this;
        }

        public int getMaxBind() {
            return maxBind;
        }

        public ManaLevelData setMaxBind(int maxBind) {
            this.maxBind = maxBind;
            return this;
        }

        public int getClockTime() {
            return clockTime;
        }

        public ManaLevelData setClockTime(int clockTime) {
            this.clockTime = clockTime;
            return this;
        }

        public Color getColor() {
            return color;
        }

        public ManaLevelData setColor(Color color) {
            this.color = color;
            return this;
        }

        public int getMaxManaContainer() {
            return maxManaContainer;
        }

        public ManaLevelData setMaxManaContainer(int maxManaContainer) {
            this.maxManaContainer = maxManaContainer;
            return this;
        }
    }

    public static ManaLevel T1;
    public static ManaLevel T2;
    public static ManaLevel T3;
    public static ManaLevel T4;
    public static ManaLevel T5;
    public static ManaLevel T6;
    public static ManaLevel T7;
    public static ManaLevel T8;
    public static ManaLevel T9;
    public static ManaLevel T10;

    public static void init() {
        T1 = new ManaLevel("T1", 1, 1, 1, 64 * 20, 1, new Color(0, 191, 255));
        T2 = new ManaLevel("T2", 2, 2, 1, 32 * 20, 2, new Color(65, 105, 225));
        T3 = new ManaLevel("T3", 3, 3, 2, 16 * 20, 3, new Color(0, 0, 255));
        T4 = new ManaLevel("T4", 4, 4, 2, 8 * 20, 4, new Color(221, 160, 221));
        T5 = new ManaLevel("T5", 5, 5, 3, 4 * 20, 5, new Color(238, 130, 238));
        T6 = new ManaLevel("T6", 6, 6, 3, 2 * 20, 6, new Color(255, 0, 255));
        T7 = new ManaLevel("T7", 7, 7, 4, 20, 7, new Color(240, 230, 140));
        T8 = new ManaLevel("T8", 8, 8, 4, 10, 8, new Color(255, 215, 0));
        T9 = new ManaLevel("T9", 9, 9, 5, 5, 9, new Color(255, 255, 0));
        T10 = new ManaLevel("T10", 10, 10, 5, 1, 10, new Color(255, 255, 255));
    }

    public static void initOreDictionary() {
        for (ManaLevel manaLevel : register) {
            manaLevel.item.forEach((k, v) -> OreDictionary.registerOre(getOreString(manaLevel, k), v));
            manaLevel.itemBlock.forEach((k, v) -> OreDictionary.registerOre(getOreString(manaLevel, k), v));
        }
    }

    public static void initRecipe() {
    }

}
