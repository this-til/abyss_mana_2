package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.common.ModTab;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;

import java.awt.*;
import java.util.Objects;

public class Ore extends RegisterBasics<Ore> {

    public static IForgeRegistry<Ore> register = null;

    public Map<OreType, Item> item = new Map<>();
    public Map<OreBlock, Block> block = new Map<>();
    public Map<OreBlock, Item> itemBlock = new Map<>();
    public Map<OreFluid, Fluid> fluid = new Map<>();
    public Map<OreFluid, Block> fluidBlock = new Map<>();
    public Map<OreFluid, Item> fluidItem = new Map<>();


    public Ore(String name, GenericParadigmMap genericParadigmMap) {
        this(new ResourceLocation(AbyssMana2.MODID, name), genericParadigmMap);
    }

    public Ore(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        super(resourceLocation, genericParadigmMap);
    }

    /***
     * 获取需要处理矿石的等级
     */
    public ManaLevel getManaLevel() {
        return getGenericParadigmMap().get(manaLevel).func();
    }

    /***
     * 获取处理所需时间
     */
    public int surplusTiem() {
        return getGenericParadigmMap().get(surplusTiem);
    }

    /***
     * 获取处理时毎刻所需灵气
     */
    public long consumeMana() {
        return getGenericParadigmMap().get(consumeMana);
    }

    /***
     * 获得单位矿内部所含灵气
     */
    public long getHasMana() {
        return getGenericParadigmMap().get(hasMana);
    }

    public Ore[] getAdditionalOutputOfWash() {
        return getGenericParadigmMap().get(additionalOutputOfWash).func();
    }

    public Ore[] getAdditionalOutputOfCentrifugal() {
        return getGenericParadigmMap().get(additionalOutputOfCentrifugal).func();
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<Ore> event) {
        event.getRegistry().register(this);
        for (OreType oreType : OreType.register) {
            Item item = oreType.getItem(this);
            this.item.put(oreType, item);
            GameData.register_impl(item);
        }
        for (OreBlock oreBlock : OreBlock.register) {
            Ore ore = this;
            Block block = oreBlock.getBlock(this);
            ItemBlock itemBlock = (ItemBlock) new ItemBlock(block) {
                @Override
                public String getItemStackDisplayName(ItemStack stack) {
                    return oreBlock instanceof OreBlock.OreMineral ? Lang.getOreBlockLang(ore) : Lang.getLang(ore, oreBlock);
                }
            }
                    .setRegistryName(Objects.requireNonNull(block.getRegistryName()))
                    .setUnlocalizedName(AbyssMana2.MODID + block.getRegistryName().getResourcePath());
            this.itemBlock.put(oreBlock, itemBlock);
            this.block.put(oreBlock, block);
            GameData.register_impl(block);
            GameData.register_impl(itemBlock);
        }
        for (OreFluid oreFluid : OreFluid.register) {
            Fluid fluid = oreFluid.getFluid(this);
            FluidRegistry.registerFluid(fluid);
            FluidRegistry.addBucketForFluid(fluid);
            this.fluid.put(oreFluid, fluid);
            ResourceLocation fluidName = new ResourceLocation(AbyssMana2.MODID, fluid.getName());
            BlockFluidClassic blockFluidClassic = (BlockFluidClassic) new BlockFluidClassic(fluid, Material.WATER)
                    .setRegistryName(fluidName)
                    .setUnlocalizedName(AbyssMana2.MODID + "." + fluidName.getResourcePath());
            ItemBlock itemBlock = (ItemBlock) new ItemBlock(blockFluidClassic)
                    .setRegistryName(fluidName)
                    .setUnlocalizedName(AbyssMana2.MODID + "." + fluidName.getResourcePath())
                    .setCreativeTab(ModTab.TAB);
            this.fluidBlock.put(oreFluid, blockFluidClassic);
            this.fluidItem.put(oreFluid, itemBlock);
            GameData.register_impl(blockFluidClassic);
            GameData.register_impl(itemBlock);
        }

    }

    /***
     * 固体灵气
     * T-1
     */
    public static Ore solidMana;

    /***
     * 余烬
     * 被灵气提取去榨干后所得
     */
    public static Ore embers;

    /***
     * 杂质
     */
    public static Ore impurity;

    /***
     * 含微弱灵气岩石
     */
    public static Ore weakMana;

    /***
     * 亲和魔力铁
     */
    public static Ore nearManaIron;

    /***
     * 通魔铁
     */
    public static Ore conductionManaIron;

    /***
     * 星云银
     */
    public static Ore starSilver;

    /***
     * 星云铁
     */
    public static Ore starIron;

    /***
     * 星云金
     */
    public static Ore starGold;

    /***
     * 星河
     */
    public static Ore starRiver;

    /***
     * 日耀
     */
    public static Ore sunlight;

    /***
     * 月耀
     */
    public static Ore moonlight;

    /***
     * uu
     */
    public static Ore uu;

    /***
     * 猫
     */
    public static Ore cat;

    /***
     * 狗
     */
    public static Ore dog;

    /***
     * 虚空辉光
     */
    public static Ore voidGlow;

    /***
     *
     */
    public static Ore DHB;

    public static void init() {
        solidMana = new Ore("solid_mana", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(16, 51, 148))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T1)
                .put_genericParadigm(surplusTiem, 32 * 20)
                .put_genericParadigm(hasMana, 184320L)
                .put_genericParadigm(consumeMana, 32L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[0])
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[0])
                .put_genericParadigm(orePrefix, "SolidMana"));

        embers = new Ore("embers", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(190, 190, 190))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T1)
                .put_genericParadigm(surplusTiem, 12 * 20)
                .put_genericParadigm(hasMana, 0L)
                .put_genericParadigm(consumeMana, 8L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[0])
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[0])
                .put_genericParadigm(orePrefix, "Embers"))
        ;
        impurity = new Ore("impurity", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(45, 45, 45))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T1)
                .put_genericParadigm(surplusTiem, 4 * 20)
                .put_genericParadigm(hasMana, 5000L)
                .put_genericParadigm(consumeMana, 8L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[0])
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[0])
                .put_genericParadigm(orePrefix, "Impurity"));

        weakMana = new Ore("weak_mana", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(150, 255, 255))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T1)
                .put_genericParadigm(surplusTiem, 32 * 20)
                .put_genericParadigm(hasMana, 12000L)
                .put_genericParadigm(consumeMana, 32L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{nearManaIron, conductionManaIron})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{nearManaIron, conductionManaIron, weakMana, weakMana})
                .put_genericParadigm(orePrefix, "WeakMana"));

        nearManaIron = new Ore("near_mana_iron", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(150, 255, 255))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T1)
                .put_genericParadigm(surplusTiem, 32 * 20)
                .put_genericParadigm(hasMana, 16000L)
                .put_genericParadigm(consumeMana, 32L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{conductionManaIron, weakMana, weakMana, weakMana})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{conductionManaIron, weakMana})
                .put_genericParadigm(orePrefix, "NearManaIron"));

        conductionManaIron = new Ore("conduction_mana_iron", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(150, 200, 255))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T1)
                .put_genericParadigm(surplusTiem, 32 * 20)
                .put_genericParadigm(hasMana, 32000L)
                .put_genericParadigm(consumeMana, 32L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{nearManaIron, weakMana, weakMana, weakMana})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{nearManaIron, weakMana})
                .put_genericParadigm(orePrefix, "ConductionManaIron"));


        starSilver = new Ore("star_silver", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(216, 215, 234))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 64 * 20)
                .put_genericParadigm(hasMana, 42000L)
                .put_genericParadigm(consumeMana, 32L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{starIron, starGold, starRiver})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{starIron, starGold, starRiver})
                .put_genericParadigm(orePrefix, "StarSilver"));

        starIron = new Ore("star_iron", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(177, 176, 192))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 64 * 20)
                .put_genericParadigm(hasMana, 42000L)
                .put_genericParadigm(consumeMana, 32L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{starSilver, starGold, starRiver})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{starSilver, starGold, starRiver})
                .put_genericParadigm(orePrefix, "StarIron"));

        starGold = new Ore("star_gold", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(255, 245, 46))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 64 * 20)
                .put_genericParadigm(hasMana, 42000L)
                .put_genericParadigm(consumeMana, 32L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{starSilver, starSilver, starRiver})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{starSilver, starSilver, starRiver})
                .put_genericParadigm(orePrefix, "StarGold"));

        starRiver = new Ore("star_river", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(65, 105, 225))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 128 * 20)
                .put_genericParadigm(hasMana, 124000L)
                .put_genericParadigm(consumeMana, 64L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{starSilver, starSilver, starRiver})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{starSilver, starSilver, starRiver})
                .put_genericParadigm(orePrefix, "StarRiver"));

        sunlight = new Ore("sunlight", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(220, 255, 0))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 128 * 20)
                .put_genericParadigm(hasMana, 124000L)
                .put_genericParadigm(consumeMana, 64L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{moonlight})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{moonlight})
                .put_genericParadigm(orePrefix, "Sunlight"));

        moonlight = new Ore("moonlight", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(122, 143, 210))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 128 * 20)
                .put_genericParadigm(hasMana, 124000L)
                .put_genericParadigm(consumeMana, 64L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{sunlight})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{sunlight})
                .put_genericParadigm(orePrefix, "Moonlight"));

        uu = new Ore("uu",  new GenericParadigmMap()
                .put_genericParadigm(color, new Color(28, 1, 77))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T3)
                .put_genericParadigm(surplusTiem, 20)
                .put_genericParadigm(hasMana, 14400000000L)
                .put_genericParadigm(consumeMana, 125L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{})
                .put_genericParadigm(orePrefix, "UU"));

        // other
        cat = new Ore("cat", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(240, 240, 240))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 128 * 20)
                .put_genericParadigm(hasMana, 124000L)
                .put_genericParadigm(consumeMana, 64L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{})
                .put_genericParadigm(orePrefix, "Cat"));

        dog = new Ore("dog", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(255, 185, 15))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 128 * 20)
                .put_genericParadigm(hasMana, 124000L)
                .put_genericParadigm(consumeMana, 64L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{})
                .put_genericParadigm(orePrefix, "Gog"));

        voidGlow = new Ore("void_glow", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(255, 255, 255))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 128 * 20)
                .put_genericParadigm(hasMana, 124000L)
                .put_genericParadigm(consumeMana, 64L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{})
                .put_genericParadigm(orePrefix, "VoidGlow"));

        DHB = new Ore("dhb", new GenericParadigmMap()
                .put_genericParadigm(color, new Color(220, 220, 220))
                .put_genericParadigm(manaLevel, () -> ManaLevel.T2)
                .put_genericParadigm(surplusTiem, 128 * 20)
                .put_genericParadigm(hasMana, 124000L)
                .put_genericParadigm(consumeMana, 64L)
                .put_genericParadigm(additionalOutputOfWash, () -> new Ore[]{})
                .put_genericParadigm(additionalOutputOfCentrifugal, () -> new Ore[]{})
                .put_genericParadigm(orePrefix, "DHB"));

    }

    public static void initOreDictionary() {
        for (Ore ore : register) {
            ore.item.forEach((oreType, item) -> OreDictionary.registerOre(getOreString(oreType, ore), item));
            ore.itemBlock.forEach((oreBlock, item) -> OreDictionary.registerOre(getOreString(oreBlock, ore), item));
        }
    }

    public static void initRecipe() {
    }

    /***
     * 加工需要晶体等级
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyManaLevelPack.Pack> manaLevel = new GenericParadigmMap.IKey.KeyManaLevelPack() {
        @Override
        public Pack _default() {
            return () -> ManaLevel.T1;
        }
    };

    /***
     * 加工消耗时间
     */
    public static final GenericParadigmMap.IKey<Integer> surplusTiem = new GenericParadigmMap.IKey.KeyInt() {
        @Override
        public Integer _default() {
            return 32 * 20;
        }
    };

    /***
     * 内含灵气
     */
    public static final GenericParadigmMap.IKey<Long> hasMana = new GenericParadigmMap.IKey.KeyLang() {
        @Override
        public Long _default() {
            return 12000L;
        }
    };

    /***
     * 加工消耗灵气
     */
    public static final GenericParadigmMap.IKey<Long> consumeMana = new GenericParadigmMap.IKey.KeyLang() {
        @Override
        public Long _default() {
            return 32L;
        }
    };

    /***
     * 矿物颜色
     */
    public static final GenericParadigmMap.IKey<Color> color = GenericParadigmMap.IKey.KeyColor._default;

    /***
     * 洗矿额外产出
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyOreListPack.Pack> additionalOutputOfWash = new GenericParadigmMap.IKey.KeyOreListPack();

    /***
     * 离心额外产出
     */
    public static final GenericParadigmMap.IKey<GenericParadigmMap.IKey.KeyOreListPack.Pack> additionalOutputOfCentrifugal = new GenericParadigmMap.IKey.KeyOreListPack();

}
