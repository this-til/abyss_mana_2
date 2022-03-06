package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

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

    public static void init() {
        grind = new ShapedType("grind") {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 矿石研磨
                    {
                        Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreBlock.lordWorld, ore), 1);
                        Map<String, Integer> fluid = new Map<>();
                        Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreBlock.lordWorld, ore, OreType.crushed, ore)) {
                            @Override
                            public ManaLevel getManaLevel() {
                                return ore.getManaLevel();
                            }

                            @Override
                            public ShapedDrive getShapedDrive() {
                                return ShapedDrive.map.get(1);
                            }

                            @Override
                            public ShapedType getShapedType() {
                                return grind;
                            }

                            @Override
                            public Map<String, Integer> item() {
                                return item;
                            }

                            @Override
                            public Map<String, Integer> fluid() {
                                return fluid;
                            }

                            @Override
                            public int surplusTiem() {
                                return ore.surplusTiem();
                            }

                            @Override
                            public long consumeMana() {
                                return ore.consumeMana();
                            }

                            @Nullable
                            @Override
                            public List<ItemStack> getOutItem() {
                                return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.crushed), 2));
                            }

                            @Nullable
                            @Override
                            public List<FluidStack> getOutFuid() {
                                return null;
                            }
                        };
                        Shaped.register.register(shaped);
                    }
                }
            }

            @SubscribeEvent
            public void _onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 锭 -> 粉
                    {
                        Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.ingot, ore), 1);
                        Map<String, Integer> fluid = new Map<>();
                        Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.ingot, ore, OreType.dust, ore)) {
                            @Override
                            public Map<String, Integer> item() {
                                return item;
                            }

                            @Override
                            public Map<String, Integer> fluid() {
                                return fluid;
                            }

                            @Override
                            public int surplusTiem() {
                                return ore.surplusTiem() / 5;
                            }

                            @Override
                            public long consumeMana() {
                                return ore.consumeMana();
                            }

                            @Override
                            public ShapedType getShapedType() {
                                return grind;
                            }

                            @Override
                            public ManaLevel getManaLevel() {
                                return ore.getManaLevel();
                            }

                            @Override
                            public ShapedDrive getShapedDrive() {
                                return ShapedDrive.map.get(2);
                            }

                            @Override
                            public List<ItemStack> getOutItem() {
                                return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.dust)));
                            }

                            @Nullable
                            @Override
                            public List<FluidStack> getOutFuid() {
                                return null;
                            }
                        };
                        Shaped.register.register(shaped);
                    }
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
                            Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(oreType, ore), 1);
                            Map<String, Integer> fluid = new Map<>();
                            Shaped shaped = new Shaped.ShapedOre.RandOutOreShaped(getOreString(oreType, ore) + "_to_mana") {
                                @Nullable
                                @Override
                                public Map<ItemStack, Float> itemRandon() {
                                    return new Map<ItemStack, Float>().put_chainable(new ItemStack(Ore.embers.item.get(OreType.dustTiny)), 0.2f);
                                }

                                @Nullable
                                @Override
                                public Map<FluidStack, Float> fluidRandon() {
                                    return null;
                                }

                                @Override
                                public Map<String, Integer> item() {
                                    return item;
                                }

                                @Override
                                public Map<String, Integer> fluid() {
                                    return fluid;
                                }

                                @Override
                                public int surplusTiem() {
                                    return ore.surplusTiem();
                                }

                                @Override
                                public long consumeMana() {
                                    return 0;
                                }

                                @Override
                                public ShapedType getShapedType() {
                                    return extractMana;
                                }

                                @Override
                                public ManaLevel getManaLevel() {
                                    return ore.getManaLevel();
                                }

                                @Override
                                public ShapedDrive getShapedDrive() {
                                    return ShapedDrive.map.get(1);
                                }
                            };
                            Shaped.register.register(shaped);
                        }
                        for (OreType oreType : _canExtractManaItemType) {
                            Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(oreType, ore), 1);
                            Map<String, Integer> fluid = new Map<>();
                            Shaped shaped = new Shaped.ShapedOre.RandOutOreShaped(getOreString(oreType, ore) + "_to_mana") {
                                @Override
                                public Map<String, Integer> item() {
                                    return item;
                                }

                                @Override
                                public Map<String, Integer> fluid() {
                                    return fluid;
                                }

                                @Override
                                public int surplusTiem() {
                                    return ore.surplusTiem();
                                }

                                @Override
                                public long consumeMana() {
                                    return 0;
                                }

                                @Override
                                public long getOutMana() {
                                    return ore.getHasMana() * 4L;
                                }

                                @Nullable
                                @Override
                                public Map<ItemStack, Float> itemRandon() {
                                    return new Map<ItemStack, Float>().put_chainable(new ItemStack(Ore.embers.item.get(OreType.dustTiny)), 0.5f);
                                }

                                @Nullable
                                @Override
                                public Map<FluidStack, Float> fluidRandon() {
                                    return null;
                                }

                                @Override
                                public ShapedType getShapedType() {
                                    return extractMana;
                                }

                                @Override
                                public ManaLevel getManaLevel() {
                                    return ore.getManaLevel();
                                }

                                @Override
                                public ShapedDrive getShapedDrive() {
                                    return ShapedDrive.map.get(2);
                                }
                            };
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
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.crushed, ore), 1);
                    Map<String, Integer> fluid = new Map<String, Integer>().put_chainable(FluidRegistry.WATER.getName(), 1000);
                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.crushed, ore, OreType.crushedPurified, ore)) {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return fluid;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.surplusTiem() * 2;
                        }

                        @Override
                        public long consumeMana() {
                            return ore.consumeMana() * 2;
                        }

                        @Override
                        public List<ItemStack> getOutItem() {
                            List<ItemStack> itemStackList = new List<>();
                            itemStackList.add(new ItemStack(ore.item.get(OreType.crushedPurified)));
                            itemStackList.add(new ItemStack(Ore.impurity.item.get(OreType.dustTiny)));
                            for (Ore additionalOutputOfWash : ore.getAdditionalOutputOfWash()) {
                                itemStackList.add(new ItemStack(additionalOutputOfWash.item.get(OreType.dustTiny)));
                            }
                            return itemStackList;
                        }

                        @Nullable
                        @Override
                        public List<FluidStack> getOutFuid() {
                            return null;
                        }

                        @Override
                        public ShapedType getShapedType() {
                            return wash;
                        }

                        @Override
                        public ManaLevel getManaLevel() {
                            return ore.getManaLevel();
                        }

                        @Override
                        public ShapedDrive getShapedDrive() {
                            return ShapedDrive.map.get(1);
                        }
                    };
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
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.crushedPurified, ore), 1);
                    Map<String, Integer> fluid = new Map<>();

                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.crushedPurified, ore, OreType.dust, ore)) {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return fluid;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.surplusTiem() * 3;
                        }

                        @Override
                        public long consumeMana() {
                            return ore.consumeMana() * 3;
                        }

                        @Override
                        public List<ItemStack> getOutItem() {
                            List<ItemStack> itemStackList = new List<>();
                            itemStackList.add(new ItemStack(ore.item.get(OreType.dust)));
                            itemStackList.add(new ItemStack(Ore.impurity.item.get(OreType.dustTiny)));
                            for (Ore additionalOutputOfWash : ore.getAdditionalOutputOfCentrifugal()) {
                                itemStackList.add(new ItemStack(additionalOutputOfWash.item.get(OreType.dustTiny)));
                            }
                            return itemStackList;
                        }

                        @Nullable
                        @Override
                        public List<FluidStack> getOutFuid() {
                            return null;
                        }

                        @Override
                        public ShapedType getShapedType() {
                            return centrifugal;
                        }

                        @Override
                        public ManaLevel getManaLevel() {
                            return ore.getManaLevel();
                        }

                        @Override
                        public ShapedDrive getShapedDrive() {
                            return ShapedDrive.map.get(1);
                        }
                    };
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
                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.ingot, ore, OreBlock.block, ore)) {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return fluid;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.surplusTiem() / 10;
                        }

                        @Override
                        public long consumeMana() {
                            return ore.consumeMana() / 10;
                        }

                        @Override
                        public List<ItemStack> getOutItem() {
                            return new List<ItemStack>().add_chainable(new ItemStack(ore.itemBlock.get(OreBlock.block)));
                        }

                        @Nullable
                        @Override
                        public List<FluidStack> getOutFuid() {
                            return null;
                        }

                        @Override
                        public ShapedType getShapedType() {
                            return pack;
                        }

                        @Override
                        public ManaLevel getManaLevel() {
                            return ManaLevel.T1;
                        }

                        @Override
                        public ShapedDrive getShapedDrive() {
                            return ShapedDrive.map.get(1);
                        }
                    };
                    Shaped.register.register(shaped);
                }
            }

            @SubscribeEvent
            //粒 -> 锭
            public void _onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.nuggets, ore), 9);
                    Map<String, Integer> fluid = new Map<>();
                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.nuggets, ore, OreType.ingot, ore)) {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return fluid;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.surplusTiem() / 10;
                        }

                        @Override
                        public long consumeMana() {
                            return ore.consumeMana() / 10;
                        }

                        @Override
                        public List<ItemStack> getOutItem() {
                            return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.ingot)));
                        }

                        @Nullable
                        @Override
                        public List<FluidStack> getOutFuid() {
                            return null;
                        }

                        @Override
                        public ShapedType getShapedType() {
                            return pack;
                        }

                        @Override
                        public ManaLevel getManaLevel() {
                            return ManaLevel.T1;
                        }

                        @Override
                        public ShapedDrive getShapedDrive() {
                            return ShapedDrive.map.get(2);
                        }
                    };
                    Shaped.register.register(shaped);
                }
            }

            @SubscribeEvent
            //小搓粉 -> 粉
            public void __onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.dustTiny, ore), 9);
                    Map<String, Integer> fluid = new Map<>();
                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.dustTiny, ore, OreType.dust, ore)) {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return fluid;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.surplusTiem() / 10;
                        }

                        @Override
                        public long consumeMana() {
                            return ore.consumeMana() / 10;
                        }

                        @Nullable
                        @Override
                        public List<ItemStack> getOutItem() {
                            return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.dust)));
                        }

                        @Nullable
                        @Override
                        public List<FluidStack> getOutFuid() {
                            return null;
                        }

                        @Override
                        public ShapedType getShapedType() {
                            return pack;
                        }

                        @Override
                        public ManaLevel getManaLevel() {
                            return ManaLevel.T1;
                        }

                        @Override
                        public ShapedDrive getShapedDrive() {
                            return ShapedDrive.map.get(3);
                        }
                    };
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
                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreBlock.block, ore, OreType.ingot, ore)) {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return fluid;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.surplusTiem() / 10;
                        }

                        @Override
                        public long consumeMana() {
                            return ore.consumeMana() / 10;
                        }

                        @Nullable
                        @Override
                        public List<ItemStack> getOutItem() {
                            return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.ingot), 9));
                        }

                        @Nullable
                        @Override
                        public List<FluidStack> getOutFuid() {
                            return null;
                        }

                        @Override
                        public ShapedType getShapedType() {
                            return unpack;
                        }

                        @Override
                        public ManaLevel getManaLevel() {
                            return ManaLevel.T1;
                        }

                        @Override
                        public ShapedDrive getShapedDrive() {
                            return ShapedDrive.map.get(1);
                        }
                    };
                    Shaped.register.register(shaped);
                }
            }

            @SubscribeEvent
            // 锭 -> 粒
            public void _onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.ingot, ore), 1);
                    Map<String, Integer> fluid = new Map<>();
                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.ingot, ore, OreType.nuggets, ore)) {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return fluid;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.surplusTiem() / 10;
                        }

                        @Override
                        public long consumeMana() {
                            return ore.consumeMana() / 10;
                        }

                        @Nullable
                        @Override
                        public List<ItemStack> getOutItem() {
                            return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.nuggets), 9));
                        }

                        @Nullable
                        @Override
                        public List<FluidStack> getOutFuid() {
                            return null;
                        }

                        @Override
                        public ShapedType getShapedType() {
                            return unpack;
                        }

                        @Override
                        public ManaLevel getManaLevel() {
                            return ManaLevel.T1;
                        }

                        @Override
                        public ShapedDrive getShapedDrive() {
                            return ShapedDrive.map.get(2);
                        }
                    };
                    Shaped.register.register(shaped);
                }
            }

            @SubscribeEvent
            // 粉 -> 小搓粉
            public void __onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreType.dust, ore), 1);
                    Map<String, Integer> fluid = new Map<>();

                    Shaped shaped = new Shaped.ShapedOre(getRecipeNameOfAToB(OreType.dust, ore, OreType.dustTiny, ore)) {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return fluid;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.surplusTiem() / 10;
                        }

                        @Override
                        public long consumeMana() {
                            return ore.consumeMana() / 10;
                        }

                        @Nullable
                        @Override
                        public List<ItemStack> getOutItem() {
                            return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.dustTiny), 9));
                        }

                        @Nullable
                        @Override
                        public List<FluidStack> getOutFuid() {
                            return null;
                        }

                        @Override
                        public ShapedType getShapedType() {
                            return unpack;
                        }

                        @Override
                        public ManaLevel getManaLevel() {
                            return ManaLevel.T1;
                        }

                        @Override
                        public ShapedDrive getShapedDrive() {
                            return ShapedDrive.map.get(3);
                        }
                    };
                    Shaped.register.register(shaped);
                }
            }

            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.unpack;
            }
        };
    }
}
