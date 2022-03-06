package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;

public abstract class ManaLevel extends RegisterBasics<ManaLevel> {

    public static IForgeRegistry<ManaLevel> register = null;

    public final Map<ManaLevelItem, Item> item = new Map<>();
    public final Map<ManaLevelBlock, Item> itemBlock = new Map<>();
    public final Map<ManaLevelBlock, Block> block = new Map<>();

    public ManaLevel(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }

    public ManaLevel(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public Color getColor() {
        return new Color(255, 255, 255);
    }

    /***
     * 返回一个等级数
     */
    public int getLevel() {
        return 1;
    }

    public int getHandle() {
        return getLevel();
    }

    public int getMaxBind() {
        return 2 + getLevel() / 2;
    }

    public int getClockTime() {
        return 128 * 20;
    }

    public int getParallelHandle() {
        return getLevel();
    }

    public long getMaxManaContainer() {
        return getLevel();
    }

    /***
     * 获得上一级
     */
    @Nullable
    public abstract ManaLevel getUp();

    /***
     * 获得下一级
     */
    @Nullable
    public abstract ManaLevel getNext();

    @SubscribeEvent
    public void register(RegistryEvent.Register<ManaLevel> event) {
        event.getRegistry().register(this);

        ManaLevel manaLevel = this;

        for (ManaLevelItem manaLevelItem : ManaLevelItem.register) {
            Item item = manaLevelItem.getItem(this);
            this.item.put(manaLevelItem, item);
            GameData.register_impl(item);
        }

        for (ManaLevelBlock manaLevelBlock : ManaLevelBlock.register) {
            Block block = manaLevelBlock.getBlock(this);
            ItemBlock itemBlock = (ItemBlock) new ItemBlock(block) {
                @Override
                public String getItemStackDisplayName(ItemStack stack) {
                    return Lang.getLang(manaLevel, manaLevelBlock);
                }
            }
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
        T1 = new ManaLevel("t1") {
            @Override
            public Color getColor() {
                return new Color(0, 191, 255);
            }

            @Override
            public int getLevel() {
                return 1;
            }

            public int getClockTime() {
                return 64 * 20;
            }

            @Nullable
            @Override
            public ManaLevel getUp() {
                return null;
            }

            @Override
            public ManaLevel getNext() {
                return T2;
            }
        };
        T2 = new ManaLevel("t2") {
            @Override
            public Color getColor() {
                return new Color(65, 105, 225);
            }

            @Override
            public int getLevel() {
                return 2;
            }

            public int getClockTime() {
                return 32 * 20;
            }

            @Override
            public ManaLevel getUp() {
                return T1;
            }


            @Override
            public ManaLevel getNext() {
                return T3;
            }
        };
        T3 = new ManaLevel("t3") {
            @Override
            public Color getColor() {
                return new Color(0, 0, 255);
            }

            @Override
            public int getLevel() {
                return 3;
            }

            public int getClockTime() {
                return 16 * 20;
            }

            @Override
            public ManaLevel getUp() {
                return T2;
            }

            @Override
            public ManaLevel getNext() {
                return T4;
            }


        };
        T4 = new ManaLevel("t4") {
            @Override
            public Color getColor() {
                return new Color(221, 160, 221);
            }

            @Override
            public int getLevel() {
                return 4;
            }

            public int getClockTime() {
                return 8 * 20;
            }

            public ManaLevel getUp() {
                return T3;
            }


            @Override
            public ManaLevel getNext() {
                return T5;
            }
        };
        T5 = new ManaLevel("t5") {
            @Override
            public Color getColor() {
                return new Color(238, 130, 238);
            }

            @Override
            public int getLevel() {
                return 8;
            }

            public int getClockTime() {
                return 4 * 20;
            }

            public ManaLevel getUp() {
                return T4;
            }


            @Override
            public ManaLevel getNext() {
                return T6;
            }
        };
        T6 = new ManaLevel("t6") {
            @Override
            public Color getColor() {
                return new Color(255, 0, 255);
            }

            @Override
            public int getLevel() {
                return 6;
            }

            public int getClockTime() {
                return 2 * 20;
            }

            public ManaLevel getUp() {
                return T5;
            }


            @Override
            public ManaLevel getNext() {
                return T7;
            }

        };
        T7 = new ManaLevel("t7") {
            @Override
            public Color getColor() {
                return new Color(240, 230, 140);
            }

            @Override
            public int getLevel() {
                return 7;
            }

            public int getClockTime() {
                return 20;
            }

            public ManaLevel getUp() {
                return T6;
            }

            @Override
            public ManaLevel getNext() {
                return T8;
            }
        };
        T8 = new ManaLevel("t8") {
            @Override
            public Color getColor() {
                return new Color(255, 215, 0);
            }

            @Override
            public int getLevel() {
                return 8;
            }

            public int getClockTime() {
                return 10;
            }

            public ManaLevel getUp() {
                return T7;
            }

            @Override
            public ManaLevel getNext() {
                return T9;
            }
        };
        T9 = new ManaLevel("t9") {
            @Override
            public Color getColor() {
                return new Color(255, 255, 0);
            }

            @Override
            public int getLevel() {
                return 9;
            }

            public int getClockTime() {
                return 5;
            }

            public ManaLevel getUp() {
                return T8;
            }

            @Override
            public ManaLevel getNext() {
                return T10;
            }

        };
        T10 = new ManaLevel("t10") {
            @Override
            public Color getColor() {
                return new Color(255, 255, 255);
            }

            @Override
            public int getLevel() {
                return 10;
            }

            public int getClockTime() {
                return 1;
            }

            public ManaLevel getUp() {
                return T9;
            }

            @Override
            public ManaLevel getNext() {
                return null;
            }
        };
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
