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

    public static void init() {
        grind = new ShapedType("grind") {
            @SubscribeEvent
            public void onEvent(ModEvent.ModEventLoad.init event) {
                for (Ore ore : Ore.register) {
                    // 矿石研磨
                    {
                        Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(OreBlock.lordWorld, ore), 1);
                        Map<String, Integer> fluid = new Map<>();

                        Shaped shaped = new Shaped(getRecipeNameOfAToB(OreBlock.lordWorld, ore, OreType.crushed, ore),
                                ShapedType.grind,
                                ore.getHandleLevel(),
                                ShapedDrive.map.get(1),
                                new Shaped.IShapedStack.OreShaped() {

                                    @Override
                                    public int surplusTiem() {
                                        return ore.getHandleTime();
                                    }

                                    @Override
                                    public int consumeMana() {
                                        return ore.getHandleMana();
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
                                    public List<ItemStack> getOutItem() {
                                        return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.crushed), 2));
                                    }

                                    @Override
                                    public List<FluidStack> getOutFuid() {
                                        return null;
                                    }
                                });
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

                        Shaped shaped = new Shaped(getRecipeNameOfAToB(OreType.ingot, ore, OreType.dust, ore),
                                ShapedType.grind,
                                ore.getHandleLevel(),
                                ShapedDrive.map.get(2), new Shaped.IShapedStack.OreShaped() {
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
                                return ore.getHandleTime() / 5;
                            }

                            @Override
                            public int consumeMana() {
                                return ore.getHandleMana();
                            }

                            @Override
                            public List<ItemStack> getOutItem() {
                                return new List<ItemStack>().add_chainable(new ItemStack(ore.item.get(OreType.dust)));
                            }

                            @Override
                            public List<FluidStack> getOutFuid() {
                                return null;
                            }
                        });
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
                            Shaped shaped = new Shaped(getOreString(oreType, ore) + "_to_mana",
                                    this,
                                    ore.getHandleLevel(),
                                    ShapedDrive.map.get(1), new Shaped.IShapedStack.OreShaped.RandOutOreShaped() {
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
                                    return ore.getHandleTime();
                                }

                                @Override
                                public int consumeMana() {
                                    return 0;
                                }

                                @Override
                                public int getOutMana() {
                                    return ore.getHasMana();
                                }

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
                            });
                            Shaped.register.register(shaped);
                        }
                        for (OreType oreType : _canExtractManaItemType) {
                            Map<String, Integer> item = new Map<String, Integer>().put_chainable(getOreString(oreType, ore), 1);
                            Map<String, Integer> fluid = new Map<>();
                            Shaped shaped = new Shaped(getOreString(oreType, ore) + "_to_mana",
                                    this,
                                    ore.getHandleLevel(),
                                    ShapedDrive.map.get(2),
                                    new Shaped.IShapedStack.OreShaped.RandOutOreShaped() {
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
                                            return ore.getHandleTime();
                                        }

                                        @Override
                                        public int consumeMana() {
                                            return 0;
                                        }

                                        @Override
                                        public int getOutMana() {
                                            return ore.getHasMana() * 4;
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
                                    });
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
                    Shaped shaped = new Shaped(getRecipeNameOfAToB(OreType.crushed, ore, OreType.crushedPurified, ore),
                            this,
                            ore.getHandleLevel(),
                            ShapedDrive.map.get(1),
                            new Shaped.IShapedStack.OreShaped() {
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
                                    return ore.getHandleTime() * 2;
                                }

                                @Override
                                public int consumeMana() {
                                    return ore.getHandleMana() * 2;
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
                            });
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

                    Shaped shaped = new Shaped(getRecipeNameOfAToB(OreType.crushedPurified, ore, OreType.crushed, ore),
                            this,
                            ore.getHandleLevel(),
                            ShapedDrive.map.get(1), new Shaped.IShapedStack.OreShaped() {
                        @Override
                        public Map<String, Integer> item() {
                            return item;
                        }

                        @Override
                        public Map<String, Integer> fluid() {
                            return null;
                        }

                        @Override
                        public int surplusTiem() {
                            return ore.getHandleTime() * 3;
                        }

                        @Override
                        public int consumeMana() {
                            return ore.getHandleMana() * 3;
                        }

                        @Override
                        public List<ItemStack> getOutItem() {
                            List<ItemStack> itemStackList = new List<>();
                            itemStackList.add(new ItemStack(ore.item.get(OreType.crushed)));
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
                    });

                }
            }

            @Override
            public ManaLevelBlock getJEIBlock() {
                return ManaLevelBlock.centrifugal;
            }
        };
    }
}
