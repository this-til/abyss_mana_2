package com.til.abyss_mana_2.common.capability;

import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.extension.Extension;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public interface IClockTime extends INBT {

    int getClockTime();

    int getCycleTime();

    void setClockTime(int clockTime);

    /***
     * 时间流逝
     * 只有这里才会触发时钟方法
     */
    default void time() {
        setClockTime(getCycleTime() - 1);
        if (getCycleTime() >= 0) {
            clockRun();
        }
    }

    /***
     * 回调
     */
    default void clockTriggerRun() {
    }

    default void clockRun() {
        clockTriggerRun();
        setClockTime(getCycleTime());
    }

    class ClockTime implements IClockTime {

        public final IManaLevel iManaLevel;

        public int time;

        public ClockTime(IManaLevel iManaLevel) {
            this.iManaLevel = iManaLevel;
        }

        @Override
        public int getClockTime() {
            return time;
        }

        @Override
        public int getCycleTime() {
            return iManaLevel.getManaLevel().getClockTime();
        }

        @Override
        public void setClockTime(int clockTime) {
            time = clockTime;
        }

        @Override
        public AllNBT.IGS<NBTBase> getNBTBase() {
            return AllNBT.iClockTime;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setInteger("time", getClockTime());
            return nbtTagCompound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            setClockTime(nbt.getInteger("time"));
        }
    }

}