package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
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

import java.awt.*;
import java.util.Objects;

public class ManaLevel extends RegisterBasics<ManaLevel> {

    public static IForgeRegistry<ManaLevel> register = null;

    public final Map<Integer, ManaLevel> map = new Map<>();
    public final Map<ManaLevelItem, Item> item = new Map<>();
    public final Map<ManaLevelBlock, Item> itemBlock = new Map<>();
    public final Map<ManaLevelBlock, Block> block = new Map<>();

    public ManaLevel(String name, GenericParadigmMap genericParadigmMap) {
        this(new ResourceLocation(AbyssMana2.MODID, name), genericParadigmMap);
    }

    public ManaLevel(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        super(resourceLocation, genericParadigmMap);
        map.put(genericParadigmMap.get(level), this);
    }

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
        T1 = new ManaLevel("t1", new GenericParadigmMap()
                .put_genericParadigm(level, 1)
                .put_genericParadigm(color, new Color(0, 191, 255))
                .put_genericParadigm(up, () -> null)
                .put_genericParadigm(next, () -> T2)
                .put_genericParadigm(thisNeedItem, () -> new Ore[]{
                        Ore.conductionManaIron,
                        Ore.nearManaIron
                }));
        T2 = new ManaLevel("t2", new GenericParadigmMap()
                .put_genericParadigm(level, 2)
                .put_genericParadigm(color, new Color(65, 105, 225))
                .put_genericParadigm(up, () -> T1)
                .put_genericParadigm(next, () -> T2)
                .put_genericParadigm(thisNeedItem, () -> new Ore[]{
                        Ore.starSilver,
                        Ore.starIron,
                        Ore.starGold,
                }));
        T3 = new ManaLevel("t3", new GenericParadigmMap()
                .put_genericParadigm(level, 3)
                .put_genericParadigm(color, new Color(0, 0, 255))
                .put_genericParadigm(up, () -> T2)
                .put_genericParadigm(next, () -> T4)
                .put_genericParadigm(thisNeedItem, () -> new Ore[]{
                        Ore.starRiver
                }));
        T4 = new ManaLevel("t4", new GenericParadigmMap()
                .put_genericParadigm(level, 4)
                .put_genericParadigm(color, new Color(221, 160, 221))
                .put_genericParadigm(up, () -> T3)
                .put_genericParadigm(next, () -> T5));
        T5 = new ManaLevel("t5", new GenericParadigmMap()
                .put_genericParadigm(level, 5)
                .put_genericParadigm(color, new Color(238, 130, 238))
                .put_genericParadigm(up, () -> T4)
                .put_genericParadigm(next, () -> T6));
        T6 = new ManaLevel("t6", new GenericParadigmMap()
                .put_genericParadigm(level, 6)
                .put_genericParadigm(color, new Color(255, 0, 255))
                .put_genericParadigm(up, () -> T5)
                .put_genericParadigm(next, () -> T7));
        T7 = new ManaLevel("t7", new GenericParadigmMap()
                .put_genericParadigm(level, 7)
                .put_genericParadigm(color, new Color(240, 230, 140))
                .put_genericParadigm(up, () -> T6)
                .put_genericParadigm(next, () -> T9));
        T8 = new ManaLevel("t8", new GenericParadigmMap()
                .put_genericParadigm(level, 8)
                .put_genericParadigm(color, new Color(255, 215, 0))
                .put_genericParadigm(up, () -> T7)
                .put_genericParadigm(next, () -> T9));
        T9 = new ManaLevel("t9", new GenericParadigmMap()
                .put_genericParadigm(level, 9)
                .put_genericParadigm(color, new Color(255, 255, 0))
                .put_genericParadigm(up, () -> T8)
                .put_genericParadigm(next, () -> T10));
        T10 = new ManaLevel("t10", new GenericParadigmMap()
                .put_genericParadigm(level, 10)
                .put_genericParadigm(color, new Color(255, 255, 255))
                .put_genericParadigm(up, () -> T9)
                .put_genericParadigm(next, () -> null));


    }

    public static void initOreDictionary() {
        for (ManaLevel manaLevel : register) {
            manaLevel.item.forEach((k, v) -> OreDictionary.registerOre(getOreString(manaLevel, k), v));
            manaLevel.itemBlock.forEach((k, v) -> OreDictionary.registerOre(getOreString(manaLevel, k), v));
        }
    }

    public static void initRecipe() {
    }

    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyManaLevelPack.Pack> next = new GenericParadigmMap.IKey.KeyManaLevelPack();

    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyManaLevelPack.Pack> up = new GenericParadigmMap.IKey.KeyManaLevelPack();

    public static final GenericParadigmMap.IKey<Integer> level = new GenericParadigmMap.IKey.KeyInt() {
        @Override
        public Integer _default() {
            return 1;
        }
    };

    public static final GenericParadigmMap.IKey<Color> color = GenericParadigmMap.IKey.KeyColor._default;

    /***
     * 当前阶段需要材料
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyOreListPack.Pack> thisNeedItem = new GenericParadigmMap.IKey.KeyOreListPack();

    /***
     * 当前阶段需要流体
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyOreListPack.Pack> thisNeedFluid = new GenericParadigmMap.IKey.KeyOreListPack();

    /***
     * 制作框架需要额外物品
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyMapStingInt.Pack> bracket_itemIn = new GenericParadigmMap.IKey.KeyMapStingInt();

    /***
     * 制作框架需要额外流体
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyMapStingInt.Pack> bracket_fluidIn = new GenericParadigmMap.IKey.KeyMapStingInt();

    /***
     * 雕刻电路时需要的锭
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyOreListPack.Pack> carving_fluidIn_ore = new GenericParadigmMap.IKey.KeyOreListPack();

    /***
     * 雕刻电路时徐亚的锭
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyMapStingInt.Pack> carving_fluidIn = new GenericParadigmMap.IKey.KeyMapStingInt();

}
