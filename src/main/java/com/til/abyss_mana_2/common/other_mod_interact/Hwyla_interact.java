package com.til.abyss_mana_2.common.other_mod_interact;

import com.til.abyss_mana_2.common.AllBlock;
import com.til.abyss_mana_2.common.capability.*;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.common.register.ShapedDrive;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
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

        moduleRegistrar.registerBodyProvider(mechanicsWailaDataProvider, TileEntity.class);
        moduleRegistrar.registerNBTProvider(mechanicsWailaDataProvider, TileEntity.class);
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

            if (iControl != null) {
                NBTTagCompound nbtTagCompound = iControl.serializeNBT();
                nbtTagCompound.setInteger("maxBind", iControl.getMaxBind());
                nbtTagCompound.setInteger("maxRange", iControl.getMaxRange());
                tag.setTag("iControl", nbtTagCompound);
            }

            if (iHandle != null) {
                NBTTagCompound nbtTagCompound = iHandle.serializeNBT();
                nbtTagCompound.setInteger("maxClockTime", iHandle.getMaxClockTime());
                nbtTagCompound.setInteger("maxParallel", iHandle.getMaxParallel());
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
                nbtTagCompound.setInteger("now", iManaHandle.getMana());
                nbtTagCompound.setInteger("max", iManaHandle.getMaxMana());
                nbtTagCompound.setInteger("rate", iManaHandle.getMaxRate());
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

            return tag;

        }
    }

}
