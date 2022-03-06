package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ShapedType extends RegisterBasics<ShapedType> {

    public static IForgeRegistry<ShapedType> register = null;

    protected String orePrefix;

    public ShapedType(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }

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

    public ShapedType setOrePrefix(String orePrefix) {
        this.orePrefix = orePrefix;
        return this;
    }

    public ShapedType(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public ManaLevelBlock getJEIBlock() {
        return ManaLevelBlock.repeater;
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<ShapedType> event) {
        event.getRegistry().register(this);
    }

    /***
     * 研磨
     */
    public static ShapedType grind;

    /***
     * 灵气提取器配方
     */
    public static ShapedType extractMana;

    /***
     * 洗涤
     */
    public static ShapedType wash;

    /***
     * 离心
     */
    public static ShapedType centrifugal;

    /***
     * 打包
     */
    public static ShapedType pack;

    /***
     * 解包
     */
    public static ShapedType unpack;

    /***
     * 组装晶体
     */
    public static ShapedType assemble;

    public static void init() {
        grind = new ShapedType("grind") {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 矿石研磨
                    Shaped shaped = new Shaped.ShapedOre(
                            getRecipeNameOfAToB(OreBlock.lordWorld, ore, OreType.crushed, ore),
                            ore.getManaLevel(),
                            this,
                            ShapedDrive.map.get(1),
                            new Map<String, Integer>().put_chainable(getOreString(OreBlock.lordWorld, ore), 1),
                            new Map<>(),
                            ore.surplusTiem(),
                            ore.consumeMana(),
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.crushed), 2)),
                            null);
                    Shaped.register.register(shaped);

                }
            }

            @SubscribeEvent
            public void _onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 锭 -> 粉
                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.ingot, ore, OreType.dust, ore),
                            ore.getManaLevel(),
                            this,
                            ShapedDrive.map.get(2),
                            new Map<String, Integer>().put_chainable(getOreString(OreType.ingot, ore), 1),
                            new Map<>(),
                            ore.surplusTiem(),
                            ore.consumeMana() / 5,
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.dust))),
                            null);

                    Shaped.register.register(shaped);
                }
            }

            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.grind;
            }
        };
        extractMana = new ShapedType("extract_mana") {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                List<OreType> canExtractManaItemType = new List<OreType>().add_chainable(OreType.dust).add_chainable(OreType.ingot);
                List<OreType> _canExtractManaItemType = new List<OreType>().add_chainable(OreType.infiltrationDust).add_chainable(OreType.infiltrationIngot);
                for (Ore ore : Ore.register) {
                    if (ore.getHasMana() > 0) {
                        for (OreType oreType : canExtractManaItemType) {
                            Shaped shaped = new Shaped.ShapedOre.RandOutOreShaped(
                                    getOreString(oreType, ore) + "_to_mana",
                                    ore.getManaLevel(),
                                    this,
                                    ShapedDrive.map.get(1),
                                    new Map<String, Integer>().put_chainable(getOreString(oreType, ore), 1),
                                    new Map<>(),
                                    ore.surplusTiem(),
                                    0,
                                    ore.getHasMana(),
                                    new Map<ItemStack, Float>().put_chainable(new ItemStack(Ore.embers.item.get(OreType.dustTiny)), 0.2f),
                                    null
                            );
                            Shaped.register.register(shaped);
                        }
                        for (OreType oreType : _canExtractManaItemType) {
                            Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(oreType, ore), 1);
                            Map<String, Integer> fluid = new Map<>();
                            Shaped shaped = new Shaped.ShapedOre.RandOutOreShaped(
                                    getOreString(oreType, ore) + "_to_mana",
                                    ore.getManaLevel(),
                                    this,
                                    ShapedDrive.map.get(1),
                                    new Map<String, Integer>().put_chainable(getOreString(oreType, ore), 1),
                                    new Map<>(),
                                    ore.surplusTiem(),
                                    0,
                                    ore.getHasMana() * 4,
                                    new Map<ItemStack, Float>().put_chainable(new ItemStack(Ore.embers.item.get(OreType.dustTiny)), 0.5f),
                                    null
                            );
                            Shaped.register.register(shaped);
                        }
                    }
                }
            }

            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.extractMana;
            }

        };
        wash = new ShapedType("wash") {

            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    List<ItemStack> outItem = new List<>();
                    outItem.add(new ItemStack(ore.item.get(OreType.crushedPurified)));
                    outItem.add(new ItemStack(Ore.impurity.item.get(OreType.dustTiny)));
                    for (Ore additionalOutputOfWash : ore.getAdditionalOutputOfWash()) {
                        outItem.add(new ItemStack(additionalOutputOfWash.item.get(OreType.dustTiny)));
                    }
                    Shaped shaped = new Shaped.ShapedOre(
                            getRecipeNameOfAToB(OreType.crushed, ore, OreType.crushedPurified, ore),
                            ore.getManaLevel(),
                            this,
                            ShapedDrive.map.get(1),
                            new Map<String, Integer>().put_chainable(getOreString(OreType.crushed, ore), 1),
                            new Map<String, Integer>().put_chainable(FluidRegistry.WATER.getName(), 1000),
                            ore.surplusTiem() * 4,
                            ore.consumeMana(),
                            0,
                            outItem,
                            null
                    );
                    Shaped.register.register(shaped);
                }
            }

            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.wash;
            }
        };
        centrifugal = new ShapedType("centrifugal") {

            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    List<ItemStack> outItems = new List<>();
                    outItems.add(new ItemStack(ore.item.get(OreType.dust)));
                    outItems.add(new ItemStack(Ore.impurity.item.get(OreType.dustTiny)));
                    for (Ore additionalOutputOfWash : ore.getAdditionalOutputOfCentrifugal()) {
                        outItems.add(new ItemStack(additionalOutputOfWash.item.get(OreType.dustTiny)));
                    }
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.crushedPurified, ore), 1);
                    Map<String, Integer> fluid = new Map<>();

                    Shaped shaped = new Shaped.ShapedOre(
                            getRecipeNameOfAToB(OreType.crushedPurified, ore, OreType.dust, ore),
                            ore.getManaLevel(),
                            this,
                            ShapedDrive.map.get(1),
                            new Map<String, Integer>().put_chainable(getOreString(OreType.crushedPurified, ore), 1),
                            new Map<>(),
                            ore.surplusTiem() * 8,
                            ore.consumeMana(),
                            0,
                            outItems,
                            null
                    );
                    Shaped.register.register(shaped);
                }
            }

            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.centrifugal;
            }
        };
        pack = new ShapedType("pack") {

            @SubscribeEvent
            // 锭 -> 块
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.ingot, ore), 9);
                    Map<String, Integer> fluid = new Map<>();
                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.ingot, ore, OreBlock.block, ore),
                            ManaLevel.T1,
                            this,
                            ShapedDrive.map.get(1),
                            new Map<String, Integer>().put_chainable(getOreString(OreType.ingot, ore), 9),
                            new Map<>(),
                            ore.surplusTiem() / 10,
                            ore.consumeMana() / 10,
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.itemBlock.get(OreBlock.block))),
                            null);
                    Shaped.register.register(shaped);
                }
            }

            @SubscribeEvent
            //粒 -> 锭
            public void _onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.nuggets, ore), 9);
                    Map<String, Integer> fluid = new Map<>();
                    Shaped shaped = new Shaped.ShapedOre(
                            getRecipeNameOfAToB(OreType.nuggets, ore, OreType.ingot, ore),
                            ManaLevel.T1,
                            this,
                            ShapedDrive.map.get(2),
                            new Map<String, Integer>().put_chainable(getOreString(OreType.nuggets, ore), 9),
                            new Map<>(),
                            ore.surplusTiem() / 10,
                            ore.consumeMana() / 10,
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.ingot))),
                            null);
                    Shaped.register.register(shaped);
                }
            }

            @SubscribeEvent
            //小搓粉 -> 粉
            public void __onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.dustTiny, ore), 9);
                    Map<String, Integer> fluid = new Map<>();
                    Shaped shaped = new Shaped.ShapedOre(
                            getRecipeNameOfAToB(OreType.dustTiny, ore, OreType.dust, ore),
                            ManaLevel.T1,
                            this,
                            ShapedDrive.map.get(3),
                            new Map<String, Integer>().put_chainable(getOreString(OreType.dustTiny, ore), 9),
                            new Map<>(),
                            ore.surplusTiem() / 10,
                            ore.consumeMana() / 10,
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.dust))),
                            null
                    );
                    Shaped.register.register(shaped);

                }
            }

            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.pack;
            }
        };
        unpack = new ShapedType("unpack") {

            @SubscribeEvent
            //块 -> 锭
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreBlock.block, ore), 1);
                    Map<String, Integer> fluid = new Map<>();
                    Shaped shaped = new Shaped.ShapedOre(
                            getRecipeNameOfAToB(OreBlock.block, ore, OreType.ingot, ore),
                            ManaLevel.T1,
                            this,
                            ShapedDrive.map.get(1),
                            new Map<String, Integer>().put_chainable(getOreString(OreBlock.block, ore), 1),
                            new Map<>(),
                            ore.surplusTiem() / 10,
                            ore.consumeMana() / 10,
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.ingot), 9)),
                            null);
                    Shaped.register.register(shaped);
                }
            }

            @SubscribeEvent
            // 锭 -> 粒
            public void _onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.ingot, ore), 1);
                    Map<String, Integer> fluid = new Map<>();
                    Shaped shaped = new Shaped.ShapedOre(
                            getRecipeNameOfAToB(OreType.ingot, ore, OreType.nuggets, ore),
                            ManaLevel.T1,
                            this,
                            ShapedDrive.map.get(2),
                            new Map<String, Integer>().put_chainable(getOreString(OreType.ingot, ore), 1),
                            new Map<>(),
                            ore.surplusTiem() / 10,
                            ore.consumeMana() / 10,
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.nuggets), 9)),
                            null
                    );
                    Shaped.register.register(shaped);
                }
            }

            @SubscribeEvent
            // 粉 -> 小搓粉
            public void __onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.dust, ore), 1);
                    Map<String, Integer> fluid = new Map<>();

                    Shaped shaped = new Shaped.ShapedOre(
                            getRecipeNameOfAToB(OreType.dust, ore, OreType.dustTiny, ore),
                            ManaLevel.T1,
                            this,
                            ShapedDrive.map.get(3),
                            new Map<String, Integer>().put_chainable(getOreString(OreBlock.block, ore), 1),
                            new Map<>(),
                            ore.surplusTiem() / 10,
                            ore.consumeMana() / 10,
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.dustTiny), 9)),
                            null);
                    Shaped.register.register(shaped);
                }
            }

            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.unpack;
            }
        };
        assemble = new ShapedType("assemble") {
            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.assemble;
            }

            /***
             * 添加支架的制作
             */
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> itemMap = new Map<String, Integer>()
                            .put_chainable(getOreString(OreType.ingot, ore), 4)
                            .put_chainable(getOreString(OreType.string, ore), 16)
                            .put_chainable("blockGlassColorless", 1);
                    Shaped shaped = new Shaped.ShapedOre(
                            getName(itemMap, getOreString(OreBlock.bracket, ore)),
                            ore.getManaLevel(),
                            this,
                            ShapedDrive.map.get(1),
                            itemMap,
                            new Map<>(),
                            ore.surplusTiem() * 16,
                            ore.consumeMana() * 4,
                            0,
                            new List<ItemStack>().add_chainable(new ItemStack(ore.itemBlock.get(OreBlock.bracket))),
                            null
                    );
                    Shaped.register.register(shaped);
                }
            }
        };
    }
}
