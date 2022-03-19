package com.til.abyss_mana_2.common.capability;

import com.til.abyss_mana_2.util.data.AllNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbyssManaModCapability implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    public final Map<Capability<?>, Object> map;
    private final List<INBT> inbtSerializables = new ArrayList<>();

    public AbyssManaModCapability(Map<Capability<?>, Object> map) {
        this.map = map;

        for (Capability<?> capability : map.keySet()) {
            Object o = map.get(capability);
            if (o instanceof INBT) {
                inbtSerializables.add((INBT) o);
            }
        }

    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return map.get(capability) != null;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        Object o = map.get(capability);
        if (o instanceof ICapabilityCallback) {
            ((ICapabilityCallback) o).run();
        }
        return (T) o;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        for (INBT inbt : inbtSerializables) {
            if (inbt != null) {
                AllNBT.IGS<NBTBase> igs = inbt.getNBTBase();
                if (igs != null) {
                    igs.set(nbtTagCompound, inbt.serializeNBT());
                }
            }
        }
        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for (INBT inbt : inbtSerializables) {
            if (inbt != null) {
                AllNBT.IGS<NBTBase> igs = inbt.getNBTBase();
                if (igs != null) {
                    NBTBase nbtBase = igs.get(nbt);
                    inbt.deserializeNBT(nbtBase instanceof NBTTagCompound ? (NBTTagCompound) nbtBase : new NBTTagCompound());
                }
            }
        }
    }

}
