package com.til.abyss_mana_2.common.capability;

import com.google.gson.JsonObject;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.common.particle.CommonParticle;
import com.til.abyss_mana_2.common.register.*;
import com.til.abyss_mana_2.util.Pos;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.data.message.player_message.PlayerMessage;
import com.til.abyss_mana_2.util.extension.Extension;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

public interface IHandle extends IControl, INBT, IThis<TileEntity>, ITickable, IManaLevel {

    /***
     * 获取所有的配方
     */
    List<ShapedType> getShapedTypes();

    void addShapedHandle(ShapedHandle shaped);

    int getClockTime();

    void setClockTime(int clockTime);

    /***
     * 获得最大触发时间刻
     */
    int getMaxClockTime();

    /***
     * 获取最大配方并行
     */
    int getParallelHandle();


    List<ShapedDrive> getShapedDrive();

    class Handle implements IHandle {

        public final List<ShapedType> shapedTypes;
        public final TileEntity tileEntity;
        public final IControl iControl;
        public final IManaLevel iManaLevel;

        /***
         * 正在生产、输出的配方
         */
        public List<ShapedHandle> shapedHandles = new List<>();

        /***
         * 时钟时间
         * 为0时触发输出输入
         */
        public int clockTime;

        public Handle(TileEntity tileEntity, List<ShapedType> shapedTypes, IControl iControl, IManaLevel iManaLevel) {
            this.shapedTypes = shapedTypes;
            this.tileEntity = tileEntity;
            this.iControl = iControl;
            this.iManaLevel = iManaLevel;
        }

        /***
         * 获取所有的配方
         */
        @Override
        public List<ShapedType> getShapedTypes() {
            return shapedTypes;
        }

        @Override
        public void addShapedHandle(ShapedHandle shaped) {
            shapedHandles.add(shaped);
            MinecraftForge.EVENT_BUS.post(new ModEvent.EventControl.EventHandle.addShapedHandle(this, shaped));
        }

        @Override
        public int getClockTime() {
            return clockTime;
        }

        @Override
        public void setClockTime(int clockTime) {
            this.clockTime = clockTime;
        }

        @Override
        public List<ShapedDrive> getShapedDrive() {
            List<ShapedDrive> list = new List<>();
            getCapability(BindType.modelStore).forEach((k, v) -> list.addAll(v.get()));
            return list;
        }

        @Override
        public void update() {
            Map<TileEntity, IManaHandle> manaIn = getCapability(BindType.manaIn);
            Map<TileEntity, IManaHandle> manaOut = getCapability(BindType.manaOut);
            shapedHandles.forEach(shapedHandle -> shapedHandle.up(this, manaIn, manaOut));
            clockTime--;
            if (clockTime <= 0) {
                clockTime = getMaxClockTime();
                Map<TileEntity, IItemHandler> itemIn = getCapability(BindType.itemIn);
                Map<TileEntity, IItemHandler> itemOut = getCapability(BindType.itemOut);
                Map<TileEntity, IFluidHandler> fluidIn = getCapability(BindType.fluidIn);
                Map<TileEntity, IFluidHandler> fluidOut = getCapability(BindType.fluidOut);

                shapedHandles.forEach(shapedHandle -> shapedHandle.clockTime(this, itemOut, fluidOut));

                List<ShapedHandle> rShapedHandle = new List<>();
                shapedHandles.forEach(h -> {
                    if (h.isEmpty()) {
                        rShapedHandle.add(h);
                    }
                });
                rShapedHandle.forEach(r -> shapedHandles.remove(r));

                if (shapedHandles.size() >= getParallelHandle()) {
                    return;
                }

                List<ShapedDrive> shapedDrives = getShapedDrive();

                List<Shaped> shapeds = new List<>();
                List<Shaped> rShaped = new List<>();
                Shaped.map.forEach((k, v) -> {
                    if (shapedTypes.contains(k)) {
                        v.forEach((_k, _v) -> {
                            if (shapedDrives.contains(_k)) {
                                shapeds.addAll(_v);
                            }
                        });
                    }
                });

                ShapedHandle shapedHandle;
                do {
                    shapedHandle = null;
                    if (!rShaped.isEmpty()) {
                        shapeds.removeAll(rShaped);
                        rShaped.clear();
                    }

                    for (Shaped shaped : shapeds) {
                        shapedHandle = shaped.get(this, getManaLevel(), itemIn, fluidIn);
                        if (shapedHandle != null) {
                            addShapedHandle(shapedHandle);
                            break;
                        } else {
                            rShaped.add(shaped);
                        }
                    }
                }
                while (shapedHandle != null && shapedHandles.size() < getParallelHandle());

            }
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            shapedHandles.clear();
            for (NBTBase nbtBase : nbt.getTagList("shapedHandles", 10)) {
                if (nbtBase instanceof NBTTagCompound) {
                    shapedHandles.add(new ShapedHandle((NBTTagCompound) nbtBase));
                }
            }
            setClockTime(nbt.getInteger("clockTime"));
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            NBTTagList nbtBases = new NBTTagList();
            for (ShapedHandle shapedHandle : shapedHandles) {
                nbtBases.appendTag(shapedHandle.serializeNBT());
            }
            nbtTagCompound.setTag("shapedHandles", nbtBases);
            nbtTagCompound.setInteger("clockTime", getClockTime());
            return nbtTagCompound;
        }

        @Override
        public int getMaxClockTime() {
            return getManaLevel().getClockTime();
        }

        @Override
        public int getParallelHandle() {
            return getManaLevel().getLevel();
        }

        /***
         * 返回自己
         */
        @Override
        public TileEntity getThis() {
            return tileEntity;
        }

        @Override
        public AllNBT.IGS<NBTBase> getNBTBase() {
            return AllNBT.iHandleNBT;
        }

        @Override
        public ManaLevel getManaLevel() {
            return iManaLevel.getManaLevel();
        }

        @Override
        public void unbundlingAll() {
            iControl.unbundlingAll();
        }

        @Override
        public PlayerMessage.MessageData binding(TileEntity tileEntity, BindType iBindType) {
            return iControl.binding(tileEntity, iBindType);
        }

        @Override
        public PlayerMessage.MessageData unBindling(TileEntity tileEntity, BindType iBindType) {
            return iControl.unBindling(tileEntity, iBindType);
        }

        @Override
        public boolean hasBundling(TileEntity tileEntity, BindType bindType) {
            return iControl.hasBundling(tileEntity, bindType);
        }

        @Override
        public List<TileEntity> getAllTileEntity(BindType iBindType) {
            return iControl.getAllTileEntity(iBindType);
        }

        @Override
        public <C> Map<TileEntity, C> getCapability(Capability<C> capability, BindType iBindType) {
            return iControl.getCapability(capability, iBindType);
        }

        @Override
        public <C> Map<TileEntity, C> getCapability(BindType.BundTypeBindCapability<C> bundTypeBindCapability) {
            return iControl.getCapability(bundTypeBindCapability);
        }

        @Override
        public int getMaxRange() {
            return iControl.getMaxRange();
        }

        @Override
        public int getMaxBind() {
            return iControl.getMaxBind();
        }
    }

    class ShapedHandle {

        public final long consumeMana;

        public final long surplusTiem;
        public long _surplusTiem;

        public Process process;

        @Nullable
        public List<ItemStack> outItem;
        @Nullable
        public List<FluidStack> outFuid;
        public long outMana;

        public ShapedHandle(long surplusTiem, long consumeMana, long outMana, @Nullable List<ItemStack> outItemStack, @Nullable List<FluidStack> outFuid) {
            this.surplusTiem = surplusTiem;
            this.consumeMana = consumeMana;
            this.outMana = outMana;
            this.outItem = outItemStack;
            this.outFuid = outFuid;
            this._surplusTiem = surplusTiem;
            process = Process.production;
        }

        public ShapedHandle(NBTTagCompound nbtTagCompound) {
            surplusTiem = nbtTagCompound.getLong("surplusTiem");
            consumeMana = nbtTagCompound.getLong("consumeMana");
            _surplusTiem = nbtTagCompound.getLong("_surplusTiem");
            process = Process.MAP.get(nbtTagCompound.getString("process"));
            if (process == null) {
                process = Process.production;
            }

            if (nbtTagCompound.hasKey("outItem")) {
                outItem = new List<>();
                for (NBTBase nbtBase : nbtTagCompound.getTagList("outItem", 10)) {
                    if (nbtBase instanceof NBTTagCompound) {
                        NBTTagCompound _n = (NBTTagCompound) nbtBase;
                        ItemStack itemStack = new ItemStack(_n);
                        if (!itemStack.isEmpty()) {
                            outItem.add(new ItemStack(_n));
                        }
                    }
                }
            }

            if (nbtTagCompound.hasKey("outFuid")) {
                outFuid = new List<>();
                for (NBTBase nbtBase : nbtTagCompound.getTagList("outFuid", 10)) {
                    if (nbtBase instanceof NBTTagCompound) {
                        NBTTagCompound _n = (NBTTagCompound) nbtBase;
                        FluidStack fluidStack = new FluidStack(FluidRegistry.getFluid(_n.getString("FluidName")), _n.getInteger("Amount"), _n.getCompoundTag("Tag"));
                        if (fluidStack.getFluid() != null && fluidStack.amount > 0) {
                            outFuid.add(fluidStack);
                        }
                    }
                }
            }
            outMana = nbtTagCompound.getLong("outMana");
        }

        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();

            nbtTagCompound.setLong("surplusTiem", surplusTiem);
            nbtTagCompound.setLong("consumeMana", consumeMana);
            nbtTagCompound.setLong("_surplusTiem", _surplusTiem);
            nbtTagCompound.setString("process", process.toString());

            if (outItem != null && !outItem.isEmpty()) {
                NBTTagList _outItem = new NBTTagList();
                for (ItemStack itemStack : outItem) {
                    _outItem.appendTag(itemStack.serializeNBT());
                }
                nbtTagCompound.setTag("outItem", _outItem);
            }

            if (outFuid != null && !outFuid.isEmpty()) {
                NBTTagList _outFuid = new NBTTagList();
                for (FluidStack fluidStack : outFuid) {
                    _outFuid.appendTag(fluidStack.writeToNBT(new NBTTagCompound()));
                }
                nbtTagCompound.setTag("outFuid", _outFuid);
            }

            nbtTagCompound.setLong("outMana", outMana);

            return nbtTagCompound;
        }

        public void up(IHandle iControl, Map<TileEntity, IManaHandle> inMana, Map<TileEntity, IManaHandle> outMana) {
            Extension.Action_4V<IHandle, ShapedHandle, Map<TileEntity, IManaHandle>, Map<TileEntity, IManaHandle>> shapedHandleListListAction_4V = upRun.get(process);
            if (shapedHandleListListAction_4V != null) {
                shapedHandleListListAction_4V.action(iControl, this, inMana, outMana);
            }
        }

        public void clockTime(IHandle iControl, Map<TileEntity, IItemHandler> outItem, Map<TileEntity, IFluidHandler> outFluid) {
            Extension.Action_4V<IHandle, ShapedHandle, Map<TileEntity, IItemHandler>, Map<TileEntity, IFluidHandler>> shapedHandleListListAction_4V = clockTimeRun.get(process);
            if (shapedHandleListListAction_4V != null) {
                shapedHandleListListAction_4V.action(iControl, this, outItem, outFluid);
            }
        }

        public boolean isEmpty() {
            return (outItem == null || outItem.isEmpty()) && (outFuid == null || outFuid.isEmpty()) && outMana == 0;
        }

        public enum Process {
            production, out, trippingOperation;

            public static final Map<String, Process> MAP = new Map<>();

            static {
                for (Process value : values()) {
                    MAP.put(value.toString(), value);
                }
            }
        }

        public final static Map<Process, Extension.Action_4V<IHandle, ShapedHandle, Map<TileEntity, IManaHandle>, Map<TileEntity, IManaHandle>>> upRun = new Map<>();

        public final static Random rand = new Random();

        static {
            upRun.put(Process.production, (c, shapedHandle, inMana, outMana) -> {
                long needGetMana = shapedHandle.consumeMana;
                for (java.util.Map.Entry<TileEntity, IManaHandle> tileEntityIManaHandleEntry : inMana.entrySet()) {
                    long mana = tileEntityIManaHandleEntry.getValue().extractMana(needGetMana);
                    needGetMana = needGetMana - mana;
                    if (rand.nextFloat() < mana / 128f) {
                        CommonParticle.MANA_TRANSFER.add(c.getThis().getWorld(), new Pos(tileEntityIManaHandleEntry.getKey().getPos()), new Pos(c.getThis().getPos()), new JsonObject());
                    }
                }
                if (needGetMana <= 0) {
                    shapedHandle._surplusTiem--;
                    if (shapedHandle._surplusTiem <= 0) {
                        shapedHandle.process = Process.out;
                    }
                } else {
                    shapedHandle.process = Process.trippingOperation;
                    shapedHandle._surplusTiem = shapedHandle.surplusTiem;
                }
            });
            upRun.put(Process.out, (c, shapedHandle, inMana, outMana) -> {
                if (shapedHandle.outMana > 0) {
                    for (java.util.Map.Entry<TileEntity, IManaHandle> tileEntityIManaHandleEntry : outMana.entrySet()) {
                        long mana = tileEntityIManaHandleEntry.getValue().addMana(shapedHandle.outMana);
                        shapedHandle.outMana = shapedHandle.outMana - mana;
                        if (rand.nextFloat() < mana / 128f) {
                            CommonParticle.MANA_TRANSFER.add(c.getThis().getWorld(), new Pos(c.getThis().getPos()), new Pos(tileEntityIManaHandleEntry.getKey().getPos()), new JsonObject());
                        }
                    }
                }
            });
        }

        public final static Map<Process, Extension.Action_4V<IHandle, ShapedHandle, Map<TileEntity, IItemHandler>, Map<TileEntity, IFluidHandler>>> clockTimeRun = new Map<>();

        static {
            clockTimeRun.put(Process.trippingOperation, (c, shapedHandle, iItemHandlers, iFluidHandlers) -> shapedHandle.process = Process.production);
            clockTimeRun.put(Process.out, (c, shapedHandle, iItemHandlers, iFluidHandlers) -> {

                if (shapedHandle.outItem != null && !shapedHandle.outItem.isEmpty()) {
                    List<ItemStack> nItemStack = new List<>();
                    for (ItemStack itemStack : shapedHandle.outItem) {
                        ItemStack needOut = itemStack;
                        for (java.util.Map.Entry<TileEntity, IItemHandler> tileEntityIItemHandlerEntry : iItemHandlers.entrySet()) {
                            ItemStack out = ItemHandlerHelper.insertItemStacked(tileEntityIItemHandlerEntry.getValue(), needOut, false);
                            if (out.getCount() < needOut.getCount()) {
                                CommonParticle.ITEM_TRANSFER.add(c.getThis().getWorld(), new Pos(c.getThis().getPos()), new Pos(tileEntityIItemHandlerEntry.getKey().getPos()), new JsonObject());
                            }
                            needOut = out;
                            if (needOut.isEmpty()) {
                                break;
                            }
                        }
                        if (!needOut.isEmpty()) {
                            nItemStack.add(needOut);
                        }
                    }
                    shapedHandle.outItem = nItemStack;
                }

                if (shapedHandle.outFuid != null && !shapedHandle.outFuid.isEmpty()) {
                    List<FluidStack> nFluidStack = new List<>();
                    for (FluidStack fluidStack : shapedHandle.outFuid) {
                        FluidStack needOut = fluidStack.copy();
                        for (java.util.Map.Entry<TileEntity, IFluidHandler> tileEntityIFluidHandlerEntry : iFluidHandlers.entrySet()) {
                            int surplus = tileEntityIFluidHandlerEntry.getValue().fill(needOut, true);
                            if (surplus > 0) {
                                JsonObject jsonObject = new JsonObject();
                                Color color = new Color(needOut.getFluid().getColor());
                                jsonObject.add("rgb", new Pos(color.getRed() / 255d, color.getGreen() / 255d, color.getBlue() / 255d).getJson());
                                for (int i = 0; i < surplus / 32; i++) {
                                    CommonParticle.FLUID_TRANSFER.add(c.getThis().getWorld(), new Pos(c.getThis().getPos()), new Pos(tileEntityIFluidHandlerEntry.getKey().getPos()), jsonObject);
                                }
                            }
                            needOut.amount = needOut.amount - surplus;
                            if (needOut.amount <= 0) {
                                break;
                            }
                        }
                        if (needOut.amount > 0) {
                            nFluidStack.add(needOut);
                        }
                    }
                    shapedHandle.outFuid = nFluidStack;
                }

            });
        }

        public static class EmptyShapedHandle extends ShapedHandle {
            public EmptyShapedHandle(long surplusTiem, long consumeMana, long outMana) {
                super(surplusTiem, consumeMana, outMana, null, null);
                process = Process.out;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }
        }
    }


}
