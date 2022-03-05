package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.client.util.Lang;
import com.til.abyss_mana_2.common.ModTab;
import com.til.abyss_mana_2.util.extension.List;
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
    public Color color;

    public Ore(String name, String ore, Color color) {
        this(new ResourceLocation(AbyssMana2.MODID, name), ore, color);
    }

    public Ore(ResourceLocation resourceLocation, String ore, Color color) {
        super(resourceLocation);
        setOrePrefix(ore);
        this.color = color;
    }

    /***
     * 获取需要处理矿石的等级
     */
    public ManaLevel getManaLevel() {
        return ManaLevel.T1;
    }

    /***
     * 获取处理所需时间
     */
    public int surplusTiem() {
        return 32 * 20;
    }

    /***
     * 获取处理时毎刻所需灵气
     */
    public long consumeMana() {
        return 32L;
    }

    /***
     * 获得单位矿内部所含灵气
     */
    public long getHasMana() {
        return 1200;
    }

    public List<Ore> getAdditionalOutputOfWash() {
        return new List<Ore>().add_chainable(this);
    }

    public List<Ore> getAdditionalOutputOfCentrifugal() {
        return new List<Ore>().add_chainable(this);
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
            ResourceLocation fluidName = new ResourceLocation(AbyssMana2.MODID, fluid.getName());
            BlockFluidClassic blockFluidClassic = (BlockFluidClassic) new BlockFluidClassic(fluid, Material.WATER)
                    .setRegistryName(fluidName)
                    .setUnlocalizedName(AbyssMana2.MODID + "." + fluidName.getResourcePath());
            ItemBlock itemBlock = (ItemBlock) new ItemBlock(blockFluidClassic)
                    .setRegistryName(fluidName)
                    .setUnlocalizedName(AbyssMana2.MODID + "." + fluidName.getResourcePath())
                    .setCreativeTab(ModTab.TAB);
            this.fluid.put(oreFluid, fluid);
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

    public static void init() {
        solidMana = new Ore("solid_mana", "SolidMana", new Color(16, 51, 148)) {
            @Override
            public long getHasMana() {
                return 640000;
            }
        };
        embers = new Ore("embers", "Embers", new Color(190, 190, 190)) {
            @Override
            public long getHasMana() {
                return 0;
            }
        };
        impurity = new Ore("impurity", "Impurity", new Color(45, 45, 45)) {
            @Override
            public long getHasMana() {
                return 200;
            }
        };
        weakMana = new Ore("weak_mana", "WeakMana", new Color(72, 80, 100));
        nearManaIron = new Ore("near_mana_iron", "NearManaIron", new Color(150, 255, 255)) {
            @Override
            public long getHasMana() {
                return 1600;
            }
        };
        conductionManaIron = new Ore("conduction_mana_iron", "ConductionManaIron", new Color(150, 200, 255)) {
            @Override
            public long getHasMana() {
                return 2200;
            }
        };
        starSilver = new Ore("star_silver", "StarSilver", new Color(216, 215, 234)) {
            @Override
            public ManaLevel getManaLevel() {
                return ManaLevel.T2;
            }

            @Override
            public long getHasMana() {
                return 3200;
            }

            @Override
            public int surplusTiem() {
                return 64 * 20;
            }

            @Override
            public long consumeMana() {
                return 128;
            }

            @Override
            public List<Ore> getAdditionalOutputOfWash() {
                return super.getAdditionalOutputOfCentrifugal().add_chainable(starIron).add_chainable(starGold).add_chainable(starRiver);
            }

            @Override
            public List<Ore> getAdditionalOutputOfCentrifugal() {
                return super.getAdditionalOutputOfCentrifugal().add_chainable(starIron).add_chainable(starGold).add_chainable(starRiver);
            }
        };
        starIron = new Ore("star_iron", "StarIron", new Color(177, 176, 192)) {
            @Override
            public ManaLevel getManaLevel() {
                return ManaLevel.T2;
            }

            @Override
            public long getHasMana() {
                return 3200;
            }

            @Override
            public int surplusTiem() {
                return 64 * 20;
            }

            @Override
            public long consumeMana() {
                return 128;
            }

            public List<Ore> getAdditionalOutputOfWash() {
                return super.getAdditionalOutputOfCentrifugal().add_chainable(starSilver).add_chainable(starGold).add_chainable(starRiver);
            }

            @Override
            public List<Ore> getAdditionalOutputOfCentrifugal() {
                return super.getAdditionalOutputOfCentrifugal().add_chainable(starSilver).add_chainable(starGold).add_chainable(starRiver);
            }
        };
        starGold = new Ore("star_gold", "StarGold", new Color(255, 245, 46)) {
            @Override
            public ManaLevel getManaLevel() {
                return ManaLevel.T2;
            }

            @Override
            public long getHasMana() {
                return 3200;
            }

            @Override
            public int surplusTiem() {
                return 64 * 20;
            }

            @Override
            public long consumeMana() {
                return 128;
            }

            public List<Ore> getAdditionalOutputOfWash() {
                return super.getAdditionalOutputOfCentrifugal().add_chainable(starSilver).add_chainable(starSilver).add_chainable(starRiver);
            }

            @Override
            public List<Ore> getAdditionalOutputOfCentrifugal() {
                return super.getAdditionalOutputOfCentrifugal().add_chainable(starSilver).add_chainable(starSilver).add_chainable(starRiver);
            }

        };
        starRiver = new Ore("star_river", "StarRiver", new Color(65, 105, 225)) {
            @Override
            public ManaLevel getManaLevel() {
                return ManaLevel.T2;
            }

            @Override
            public int surplusTiem() {
                return 128 * 20;
            }

            @Override
            public long consumeMana() {
                return 12800;
            }

            @Override
            public long getHasMana() {
                return 256;
            }
        };

        // other
        cat = new Ore("cat", "Cat", new Color(240, 240, 240));
        dog = new Ore("dog", "Gog", new Color(255, 185, 15));
        voidGlow = new Ore("void_glow", "VoidGlow", new Color(255, 255, 255));

    }

    public static void initOreDictionary() {
        for (Ore ore : register) {
            ore.item.forEach((oreType, item) -> OreDictionary.registerOre(getOreString(oreType, ore), item));
            ore.itemBlock.forEach((oreBlock, item) -> OreDictionary.registerOre(getOreString(oreBlock, ore), item));
        }
    }

    public static void initRecipe() {
    }
}
