package com.til.abyss_mana_2.common.register;

import com.google.gson.JsonObject;
import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.capability.IHandle;
import com.til.abyss_mana_2.common.particle.CommonParticle;
import com.til.abyss_mana_2.util.Pos;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class Shaped extends RegisterBasics<Shaped> {

    public static IForgeRegistry<Shaped> register = null;
    public static Map<ShapedType, Map<ShapedDrive, List<Shaped>>> map = new Map<>();

    public final ShapedType shapedType;
    public final ManaLevel manaLevel;
    public final ShapedDrive shapedDrive;

    public final IShapedStack shapedStack;

    public Shaped(String name, ShapedType shapedType, ManaLevel manaLevel, ShapedDrive shapedDrive, IShapedStack shapedStack) {
        this(new ResourceLocation(AbyssMana2.MODID, name), shapedType, manaLevel, shapedDrive, shapedStack);
    }

    public Shaped(ResourceLocation resourceLocation, ShapedType shapedType, ManaLevel manaLevel, ShapedDrive shapedDrive, IShapedStack shapedStack) {
        super(resourceLocation);
        this.shapedType = shapedType;
        this.manaLevel = manaLevel;
        this.shapedStack = shapedStack;
        this.shapedDrive = shapedDrive;
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<Shaped> event) {
        event.getRegistry().register(this);
    }

    public interface IShapedStack {

        int surplusTiem();

        int consumeMana();

        default int getOutMana() {
            return 0;
        }

        @Nullable
        List<ItemStack> getOutItem();

        @Nullable
        List<FluidStack> getOutFuid();

        default IJEIShaped getIJEIShaped() {
            return IJEIShaped.empty;
        }

        IHandle.ShapedHandle get(IHandle iControl, ManaLevel manaLevel, Map<TileEntity, IItemHandler> items, Map<TileEntity, IFluidHandler> fluids);

        abstract class OreShaped implements IShapedStack {

            public abstract Map<String, Integer> item();

            public abstract Map<String, Integer> fluid();

            @Override
            public IHandle.ShapedHandle get(IHandle iControl, ManaLevel manaLevel, Map<TileEntity, IItemHandler> iItemHandlers, Map<TileEntity, IFluidHandler> fluidHandlers) {

                if (item().isEmpty() && fluid().isEmpty()) {
                    return new IHandle.ShapedHandle.EmptyShapedHandle(surplusTiem(), consumeMana(), getOutMana());
                }

                if ((iItemHandlers.isEmpty() && !item().isEmpty()) || (fluidHandlers.isEmpty() && !fluid().isEmpty())) {
                    return null;
                }

                if (extractFluid(iControl, fluidHandlers, true)) {
                    for (java.util.Map.Entry<TileEntity, IItemHandler> tileEntityIItemHandlerEntry : iItemHandlers.entrySet()) {
                        if (extractItem(iControl, tileEntityIItemHandlerEntry, true)) {
                            extractFluid(iControl, fluidHandlers, false);
                            extractItem(iControl, tileEntityIItemHandlerEntry, false);
                            return new IHandle.ShapedHandle(surplusTiem() / manaLevel.manaLevelData.getLevel(), consumeMana() * manaLevel.manaLevelData.getLevel(), getOutMana(), getOutItem(), getOutFuid());
                        }
                    }
                }
                return null;
            }

            protected boolean extractItem(IHandle iControl, java.util.Map.Entry<TileEntity, IItemHandler> entry, boolean isSimulated) {
                for (java.util.Map.Entry<String, Integer> stringIntegerEntry : item().entrySet()) {
                    int needOreId = OreDictionary.getOreID(stringIntegerEntry.getKey());
                    int needItem = stringIntegerEntry.getValue();

                    for (int i = 0; i < entry.getValue().getSlots(); i++) {
                        ItemStack oldItemStack = entry.getValue().getStackInSlot(i);
                        if (oldItemStack.isEmpty()) {
                            continue;
                        }
                        if (hasOre(oldItemStack, needOreId)) {
                            ItemStack outItemStack = entry.getValue().extractItem(i, needItem, isSimulated);
                            needItem = needItem - outItemStack.getCount();
                            if (!isSimulated) {
                                CommonParticle.ITEM_TRANSFER.add(iControl.getThis().getWorld(), new Pos(entry.getKey().getPos()), new Pos(iControl.getThis().getPos()), new JsonObject());
                            }
                        }
                        if (needItem == 0) {
                            break;
                        }
                    }
                    if (needItem > 0) {
                        return false;
                    }
                }
                return true;
            }

            protected boolean extractFluid(IHandle iControl, Map<TileEntity, IFluidHandler> fluidHandlers, boolean isSimulated) {
                for (java.util.Map.Entry<String, Integer> stringIntegerEntry : fluid().entrySet()) {
                    int need = stringIntegerEntry.getValue();
                    for (java.util.Map.Entry<TileEntity, IFluidHandler> tileEntityIFluidHandlerEntry : fluidHandlers.entrySet()) {
                        FluidStack out = tileEntityIFluidHandlerEntry.getValue().drain(new FluidStack(FluidRegistry.getFluid(stringIntegerEntry.getKey()), need), !isSimulated);
                        if (out != null) {
                            need = need - out.amount;
                            if (!isSimulated) {
                                JsonObject jsonObject = new JsonObject();
                                Color color = new Color(out.getFluid().getColor());
                                jsonObject.add("rgb", new Pos(color.getRed() / 255d, color.getGreen() / 255d, color.getBlue() / 255d).getJson());
                                for (int i = 0; i < out.amount / 32; i++) {
                                    CommonParticle.FLUID_TRANSFER.add(iControl.getThis().getWorld(), new Pos(tileEntityIFluidHandlerEntry.getKey().getPos()), new Pos(iControl.getThis().getPos()), jsonObject);
                                }
                            }
                            if (need == 0) {
                                break;
                            }
                        }
                    }
                    if (need > 0) {
                        return false;
                    }
                }
                return true;
            }

            protected boolean hasOre(ItemStack itemStack, int oreId) {
                for (int oreID : OreDictionary.getOreIDs(itemStack)) {
                    if (oreID == oreId) {
                        return true;
                    }
                }
                return false;

            }

            @Override
            public IJEIShaped getIJEIShaped() {
                return new IJEIShaped() {
                    @Override
                    public java.util.List<java.util.List<ItemStack>> getItemIn() {
                        Map<String, Integer> item = item();
                        if (item.isEmpty()) {
                            return null;
                        }
                        java.util.List<java.util.List<ItemStack>> list = new ArrayList<>();
                        item.forEach((k, v) -> {
                            NonNullList<ItemStack> itemStackNonNullList = OreDictionary.getOres(k);
                            java.util.List<ItemStack> _list = new ArrayList<>();
                            if (!itemStackNonNullList.isEmpty()) {
                                itemStackNonNullList.forEach(s -> _list.add(new ItemStack(s.getItem(), v)));
                            }
                            list.add(_list);
                        });
                        return list;
                    }

                    @Override
                    public java.util.List<java.util.List<FluidStack>> getFluidIn() {
                        Map<String, Integer> fluid = fluid();
                        if (fluid.isEmpty()) {
                            return null;
                        }
                        java.util.List<java.util.List<FluidStack>> list = new ArrayList<>();
                        fluid.forEach((k, v) -> {
                            Fluid _fluid = FluidRegistry.getFluid(k);
                            if (_fluid != null) {
                                list.add(new List<FluidStack>().add_chainable(new FluidStack(_fluid, v)));
                            }
                        });
                        return list;
                    }

                    @Override
                    public java.util.List<java.util.List<ItemStack>> getItemOut() {

                        List<ItemStack> out = getOutItem();
                        if (out == null) {
                            return null;
                        }

                        java.util.List<java.util.List<ItemStack>> list = new List<>();
                        for (ItemStack itemStack : out) {
                            list.add(new List<ItemStack>().add_chainable(itemStack));
                        }
                        return list;
                    }

                    @Override
                    public java.util.List<java.util.List<FluidStack>> getFluidOut() {
                        List<FluidStack> out = getOutFuid();
                        if (out == null) {
                            return null;
                        }

                        java.util.List<java.util.List<FluidStack>> list = new List<>();
                        for (FluidStack fluidStack : out) {
                            list.add(new List<FluidStack>().add_chainable(fluidStack));
                        }
                        return list;
                    }
                };
            }

            public abstract static class RandOutOreShaped extends OreShaped {

                Random random = new Random();

                @Nullable
                public abstract Map<ItemStack, Float> itemRandon();

                @Nullable
                public abstract Map<FluidStack, Float> fluidRandon();

                @Nullable
                @Override
                public List<ItemStack> getOutItem() {
                    Map<ItemStack, Float> itemRandon = this.itemRandon();
                    if (itemRandon == null) {
                        return null;
                    }
                    List<ItemStack> itemStackList = new List<>();
                    itemRandon.forEach((k, v) -> {
                        if (random.nextFloat() < v) {
                            itemStackList.add(k.copy());
                        }
                    });
                    return itemStackList;
                }

                @Nullable
                @Override
                public List<FluidStack> getOutFuid() {
                    Map<FluidStack, Float> fluidRandon = this.fluidRandon();
                    if (fluidRandon == null) {
                        return null;
                    }
                    List<FluidStack> fluidStackList = new List<>();
                    fluidRandon.forEach((k, v) -> {
                        if (random.nextFloat() < v) {
                            fluidStackList.add(k.copy());
                        }
                    });
                    return fluidStackList;
                }

                @Override
                public IJEIShaped getIJEIShaped() {
                    return new IJEIShaped() {
                        @Override
                        public java.util.List<java.util.List<ItemStack>> getItemIn() {
                            Map<String, Integer> item = item();
                            if (item.isEmpty()) {
                                return null;
                            }
                            java.util.List<java.util.List<ItemStack>> list = new ArrayList<>();
                            item.forEach((k, v) -> {
                                NonNullList<ItemStack> itemStackNonNullList = OreDictionary.getOres(k);
                                java.util.List<ItemStack> _list = new ArrayList<>();
                                if (!itemStackNonNullList.isEmpty()) {
                                    itemStackNonNullList.forEach(s -> _list.add(new ItemStack(s.getItem(), v)));
                                }
                                list.add(_list);
                            });
                            return list;
                        }

                        @Override
                        public java.util.List<java.util.List<FluidStack>> getFluidIn() {
                            Map<String, Integer> fluid = fluid();
                            if (fluid.isEmpty()) {
                                return null;
                            }
                            java.util.List<java.util.List<FluidStack>> list = new ArrayList<>();
                            fluid.forEach((k, v) -> {
                                Fluid _fluid = FluidRegistry.getFluid(k);
                                if (_fluid != null) {
                                    list.add(new List<FluidStack>().add_chainable(new FluidStack(_fluid, v)));
                                }
                            });
                            return list;
                        }

                        @Override
                        public java.util.List<java.util.List<ItemStack>> getItemOut() {
                            Map<ItemStack, Float> out = itemRandon();
                            if (out == null) {
                                return null;
                            }
                            java.util.List<java.util.List<ItemStack>> list = new List<>();
                            out.forEach((k, v) -> {
                                ItemStack itemStack = k.copy();
                                NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
                                if (nbtTagCompound == null) {
                                    nbtTagCompound = new NBTTagCompound();
                                    itemStack.setTagCompound(nbtTagCompound);
                                }
                                nbtTagCompound.setFloat("probability", v);
                                list.add(new List<ItemStack>().add_chainable(itemStack));
                            });
                            return list;
                        }

                        @Override
                        public java.util.List<java.util.List<FluidStack>> getFluidOut() {
                            Map<FluidStack, Float> out = fluidRandon();
                            if (out == null) {
                                return null;
                            }
                            java.util.List<java.util.List<FluidStack>> list = new List<>();
                            out.forEach((k, v) -> {
                                FluidStack fluidStack = new FluidStack(k.getFluid(), k.amount, k.tag);
                                NBTTagCompound nbtTagCompound = fluidStack.tag;
                                if (nbtTagCompound == null) {
                                    nbtTagCompound = new NBTTagCompound();
                                    fluidStack.tag = nbtTagCompound;
                                }
                                nbtTagCompound.setFloat("probability", v);
                                list.add(new List<FluidStack>().add_chainable(fluidStack));
                            });
                            return list;
                        }
                    };
                }
            }
        }
    }

    public interface IJEIShaped {

        IJEIShaped empty = new IJEIShaped() {
        };

        @Nullable
        default java.util.List<java.util.List<ItemStack>> getItemIn() {
            return null;
        }

        @Nullable
        default java.util.List<java.util.List<FluidStack>> getFluidIn() {
            return null;
        }

        @Nullable
        default java.util.List<java.util.List<ItemStack>> getItemOut() {
            return null;
        }

        @Nullable
        default java.util.List<java.util.List<FluidStack>> getFluidOut() {
            return null;
        }

    }

    public static void init() {

    }

}
