package com.til.abyss_mana_2.common.capability;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class AllCapability {

    @CapabilityInject(IControl.class)
    public static Capability<IControl> I_CONTROL = null;

    @CapabilityInject(IManaHandle.class)
    public static Capability<IManaHandle> I_MANA_HANDEL = null;

    @CapabilityInject(IShapedDrive.class)
    public static Capability<IShapedDrive> I_SHAPED_DRIVE = null;

    @CapabilityInject(IHandle.class)
    public static Capability<IHandle> I_HANDEL = null;

    @CapabilityInject(IManaLevel.class)
    public static Capability<IManaLevel> I_MANA_LEVEL = null;

    public AllCapability() {
        CapabilityManager.INSTANCE.register(IControl.class, new Capability.IStorage<IControl>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IControl> capability, IControl instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IControl> capability, IControl instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(IManaHandle.class, new Capability.IStorage<IManaHandle>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IManaHandle> capability, IManaHandle instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IManaHandle> capability, IManaHandle instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(IShapedDrive.class, new Capability.IStorage<IShapedDrive>() {
            @org.jetbrains.annotations.Nullable
            @Override
            public NBTBase writeNBT(Capability<IShapedDrive> capability, IShapedDrive instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IShapedDrive> capability, IShapedDrive instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(IHandle.class, new Capability.IStorage<IHandle>() {
            @org.jetbrains.annotations.Nullable
            @Override
            public NBTBase writeNBT(Capability<IHandle> capability, IHandle instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IHandle> capability, IHandle instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(IManaLevel.class, new Capability.IStorage<IManaLevel>() {
            @org.jetbrains.annotations.Nullable
            @Override
            public NBTBase writeNBT(Capability<IManaLevel> capability, IManaLevel instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IManaLevel> capability, IManaLevel instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> null);
    }

    @SubscribeEvent
    public void addTileEntityCapability(AttachCapabilitiesEvent<TileEntity> event) {
        Map<Capability<?>, Object> map = new Map<>();
        TileEntity tileEntity = event.getObject();
        if (tileEntity instanceof ITileEntityType) {
            ITileEntityType iTileEntityType = (ITileEntityType) tileEntity;
            map = iTileEntityType.getAllCapabilities(event, map);
        }
        event.addCapability(new ResourceLocation(AbyssMana2.MODID, "bind"), new AbyssManaModCapability(map));
    }

    @SubscribeEvent
    public void addEntityCapability(AttachCapabilitiesEvent<Entity> event) {
    }

}
