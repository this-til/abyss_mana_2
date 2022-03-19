package com.til.abyss_mana_2.common.other_mod_interact;

import com.til.abyss_mana_2.common.AllBlock;
import com.til.abyss_mana_2.common.capability.*;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.common.register.BindType;
import com.til.abyss_mana_2.common.register.ManaLevelBlock;
import com.til.abyss_mana_2.common.register.ShapedDrive;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@EventInteractClassTag(modID = "waila", value = Side.SERVER)
public class Hwyla_interact {

    @SubscribeEvent
    public static void onEvent(ModEvent.ModEventLoad.postInit event) {

        ModuleRegistrar moduleRegistrar = ModuleRegistrar.instance();
        MechanicsWailaDataProvider mechanicsWailaDataProvider = new MechanicsWailaDataProvider();
        moduleRegistrar.registerHeadProvider(mechanicsWailaDataProvider, AllBlock.MechanicsBlock.class);
        moduleRegistrar.registerBodyProvider(mechanicsWailaDataProvider, AllBlock.MechanicsBlock.class);
        moduleRegistrar.registerTailProvider(mechanicsWailaDataProvider, AllBlock.MechanicsBlock.class);

        moduleRegistrar.registerBodyProvider(mechanicsWailaDataProvider, ManaLevelBlock.EmptyTile.class);
        moduleRegistrar.registerNBTProvider(mechanicsWailaDataProvider, ManaLevelBlock.EmptyTile.class);

        VoidCaseProvider voidCaseProvider = new VoidCaseProvider();
        moduleRegistrar.registerBodyProvider(voidCaseProvider, ManaLevelBlock.VoidCase.class);
        moduleRegistrar.registerNBTProvider(voidCaseProvider, ManaLevelBlock.VoidCase.class);
    }

    public static class MechanicsWailaDataProvider implements IWailaDataProvider {

        @NotNull
        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tag, World world, BlockPos pos) {
            IControl iControl = tileEntity.getCapability(AllCapability.I_CONTROL, null);
            IHandle iHandle = tileEntity.getCapability(AllCapability.I_HANDEL, null);
            IManaHandle iManaHandle = tileEntity.getCapability(AllCapability.I_MANA_HANDEL, null);
            IShapedDrive iShapedDrive = tileEntity.getCapability(AllCapability.I_SHAPED_DRIVE, null);
            IManaLevel iManaLevel = tileEntity.getCapability(AllCapability.I_MANA_LEVEL, null);
            IClockTime iClockTime = tileEntity.getCapability(AllCapability.I_CLOCK_TIME, null);
            IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            IFluidHandler iFluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

            if (iControl != null) {
                NBTTagCompound nbtTagCompound = iControl.serializeNBT();
                NBTTagList shapedDriveNBT = new NBTTagList();
                for (BindType bindType : iControl.getCanBindType()) {
                    shapedDriveNBT.appendTag(new NBTTagString(Objects.requireNonNull(bindType.getRegistryName()).toString()));
                }
                nbtTagCompound.setTag("bindTypeList", shapedDriveNBT);
                nbtTagCompound.setInteger("maxBind", iControl.getMaxBind());
                nbtTagCompound.setInteger("maxRange", iControl.getMaxRange());
                tag.setTag("iControl", nbtTagCompound);
            }

            if (iHandle != null) {
                NBTTagCompound nbtTagCompound = iHandle.serializeNBT();
                nbtTagCompound.setInteger("maxParallel", iHandle.getParallelHandle());
                {
                    NBTTagList shapedDriveNBT = new NBTTagList();
                    iHandle.getShapedDrive().forEach(i -> shapedDriveNBT.appendTag(new NBTTagInt(ShapedDrive.map.getKey(i))));
                    nbtTagCompound.setTag("shapedDrive", shapedDriveNBT);
                }
                {
                    NBTTagList shapedDriveNBT = new NBTTagList();
                    iHandle.getShapedTypes().forEach(i -> shapedDriveNBT.appendTag(new NBTTagString(Objects.requireNonNull(i.getRegistryName()).toString())));
                    nbtTagCompound.setTag("shapedTypes", shapedDriveNBT);
                }
                tag.setTag("iHandle", nbtTagCompound);
            }

            if (iManaHandle != null) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setLong("now", iManaHandle.getMana());
                nbtTagCompound.setLong("max", iManaHandle.getMaxMana());
                nbtTagCompound.setLong("rate", iManaHandle.getMaxRate());
                tag.setTag("iManaHandle", nbtTagCompound);
            }

            if (iShapedDrive != null) {
                NBTTagList nbtTagList = new NBTTagList();
                iShapedDrive.get().forEach(i -> nbtTagList.appendTag(new NBTTagInt(ShapedDrive.map.getKey(i))));
                tag.setTag("iShapedDrive", nbtTagList);
            }

            if (iManaLevel != null) {
                tag.setTag("iManaLevel", new NBTTagString(Objects.requireNonNull(iManaLevel.getManaLevel().getRegistryName()).toString()));
            }

            if (iClockTime != null) {
                NBTTagCompound nbtTagCompound = iClockTime.serializeNBT();
                nbtTagCompound.setInteger("cycleTime", iClockTime.getCycleTime());
                tag.setTag("iClockTime", nbtTagCompound);
            }

            if (itemHandler != null) {
                NBTTagList nbtTagList = new NBTTagList();
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack itemStack = itemHandler.getStackInSlot(i);
                    if (!itemStack.isEmpty()) {
                        nbtTagList.appendTag(itemStack.serializeNBT());
                    }
                }
                tag.setTag("itemHandler", nbtTagList);
            }

            if (iFluidHandler != null) {
                NBTTagList nbtTagList = new NBTTagList();
                for (IFluidTankProperties tankProperty : iFluidHandler.getTankProperties()) {
                    FluidStack fluidStack = tankProperty.getContents();
                    if (fluidStack != null) {
                        nbtTagList.appendTag(fluidStack.writeToNBT(new NBTTagCompound()));
                    }
                }
                tag.setTag("iFluidHandler", nbtTagList);
            }

            return tag;

        }
    }

    public static class VoidCaseProvider implements IWailaDataProvider {
        @NotNull
        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tag, World world, BlockPos pos) {
            if (tileEntity instanceof ManaLevelBlock.VoidCase) {
                ManaLevelBlock.VoidCase voidCase = (ManaLevelBlock.VoidCase) tileEntity;
                tag.setInteger("VoidCaseAmount", voidCase.voidCaseHandler.getStackSize());
            }
            return tag;
        }
    }

}
