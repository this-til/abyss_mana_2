package com.til.abyss_mana_2.common.capability;

import com.til.abyss_mana_2.util.data.AllNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface INBT extends INBTSerializable<NBTTagCompound> {

    AllNBT.IGS<NBTBase> getNBTBase();

}
