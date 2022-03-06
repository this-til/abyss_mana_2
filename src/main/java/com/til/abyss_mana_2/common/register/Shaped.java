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


public abstract class Shaped extends RegisterBasics<Shaped> {

    public static IForgeRegistry<Shaped> register = null;
    public static Map<ShapedType, Map<ShapedDrive, List<Shaped>>> map = new Map<>();


    public Shaped(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }

    public Shaped(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<Shaped> event) {
        event.getRegistry().register(this);
    }

    public abstract int surplusTiem();

    public abstract long consumeMana();

    public long getOutMana() {
        return 0;
    }

    public abstract ShapedType getShapedType();

    public abstract ManaLevel getManaLevel();

    public abstract ShapedDrive getShapedDrive();

    @Nullable
    public abstract List<ItemStack> getOutItem();

    @Nullable
    public abstract List<FluidStack> getOutFuid();

    public IJEIShaped getIJEIShaped() {
        return IJEIShaped.empty;
    }

    public abstract IHandle.ShapedHandle get(IHandle iControl, ManaLevel manaLevel, Map<TileEntity, IItemHandler> items, Map<TileEntity, IFluidHandler> fluids);

    public static class ShapedOre extends Shaped {

        public ManaLevel manaLevel;

        Map<String, Integer> item;
        Map<String, Integer> fluid;
        ShapedType shapedType;
        ShapedDrive shapedDrive;
        int surplusTiem;
        long consumeMana;
        long outMana;
        @Nullable
        List<ItemStack> outItem;
        @Nullable
        List<FluidStack> outFuid;

        public ShapedOre(ResourceLocation resourceLocation, ManaLevel manaLevel, ShapedType shapedType, ShapedDrive shapedDrive,
                         Map<String, Integer> item, Map<String, Integer> fluid, int surplusTiem, long consumeMana,
                         long outMana, @Nullable List<ItemStack> outItem, @Nullable List<FluidStack> outFuid) {
            super(resourceLocation);
            this.manaLevel = manaLevel;
            this.shapedDrive = shapedDrive;
            this.shapedType = shapedType;
            this.fluid = fluid;
            this.item = item;
            this.surplusTiem = surplusTiem;
            this.consumeMana = consumeMana;
            this.outMana = outMana;
            this.outItem = outItem;
            this.outFuid = outFuid;
        }

        public ShapedOre(String resourceLocation, ManaLevel manaLevel, ShapedType shapedType, ShapedDrive shapedDrive,
                         Map<String, Integer> item, Map<String, Integer> fluid, int surplusTiem, long consumeMana,
                         long outMana, @Nullable List<ItemStack> outItem, @Nullable List<FluidStack> outFuid) {
            this(new ResourceLocation(AbyssMana2.MODID, resourceLocation), manaLevel, shapedType, shapedDrive, item,
                    fluid, surplusTiem, consumeMana, outMana, outItem, outFuid);
        }

        public Map<String, Integer> item() {
            return item;
        }

        public Map<String, Integer> fluid() {
            return fluid;
        }

        @Override
        public int surplusTiem() {
            return surplusTiem;
        }

        @Override
        public long consumeMana() {
            return consumeMana;
        }

        @Override
        public ShapedType getShapedType() {
            return shapedType;
        }

        @Override
        public ManaLevel getManaLevel() {
            return manaLevel;
        }

        @Override
        public ShapedDrive getShapedDrive() {
            return shapedDrive;
        }

        @Nullable
        @Override
        public List<ItemStack> getOutItem() {
            return outItem != null ? outItem.copy() : new List<>();
        }

        @Nullable
        @Override
        public List<FluidStack> getOutFuid() {
            return outFuid != null ? outFuid.copy() : new List<>();
        }

        @Override
        public long getOutMana() {
            return outMana;
        }

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
                        return new IHandle.ShapedHandle(surplusTiem() / manaLevel.getLevel(), consumeMana() * (long) manaLevel.getLevel(), getOutMana(), getOutItem(), getOutFuid());
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

        public static class RandOutOreShaped extends ShapedOre {

            @Nullable Map<ItemStack, Float> outItemRandon;
            @Nullable Map<FluidStack, Float> outFluidRandon;

            public RandOutOreShaped(ResourceLocation resourceLocation, ManaLevel manaLevel, ShapedType shapedType, ShapedDrive shapedDrive, Map<String, Integer> item, Map<String, Integer> fluid, int surplusTiem, long consumeMana, long outMana,@Nullable Map<ItemStack, Float> outItemRandon,@Nullable Map<FluidStack, Float> outFluidRandon) {
                super(resourceLocation, manaLevel, shapedType, shapedDrive, item, fluid, surplusTiem, consumeMana, outMana, null, null);
            }

            public RandOutOreShaped(String resourceLocation, ManaLevel manaLevel, ShapedType shapedType, ShapedDrive shapedDrive, Map<String, Integer> item, Map<String, Integer> fluid, int surplusTiem, long consumeMana, long outMana,@Nullable Map<ItemStack, Float> outItemRandon, @Nullable Map<FluidStack, Float> outFluidRandon) {
                this(new ResourceLocation(AbyssMana2.MODID, resourceLocation), manaLevel, shapedType, shapedDrive, item, fluid, surplusTiem, consumeMana, outMana, outItemRandon, outFluidRandon);
            }

            Random random = new Random();

            @Nullable
            public Map<ItemStack, Float> itemRandon() {
                return outItemRandon;
            }

            @Nullable
            public Map<FluidStack, Float> fluidRandon() {
                return outFluidRandon;
            }

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
