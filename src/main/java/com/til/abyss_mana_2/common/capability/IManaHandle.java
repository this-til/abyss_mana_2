package com.til.abyss_mana_2.common.capability;

import com.til.abyss_mana_2.common.register.ManaLevel;
import com.til.abyss_mana_2.util.data.AllNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface IManaHandle extends IThis<TileEntity>, INBT, IManaLevel {

    /***
     * 返回最大容量
     */
    long getMaxMana();

    /***
     * 返回当前容量
     */
    long getMana();

    /***
     * 返回剩余空间
     */
    default long getRemainMana() {
        return Math.max(this.getMaxMana() - this.getMana(), 0);
    }

    /***
     * 返回最大的提取速度
     */
    long getMaxRate();

    /***
     * 添加灵气
     * 返返回加入了多少
     */
    long addMana(long mana);

    /***
     *  抽取灵气
     *  返回提取了多少
     */
    long extractMana(long demand);

    class ManaHandle implements IManaHandle {

        public long mana;
        public final TileEntity tileEntity;
        public final IManaLevel iManaLevel;

        public ManaHandle(TileEntity tileEntity, IManaLevel iManaLevel) {
            this.tileEntity = tileEntity;
            this.iManaLevel = iManaLevel;
        }

        /***
         * 返回最大容量
         */
        @Override
        public long getMaxMana() {
            return 160000 * getManaLevel().getMaxManaContainer();
        }

        /***
         * 返回当前容量
         */
        @Override
        public long getMana() {
            return mana;
        }

        /***
         * 返回最大的提取速度
         */
        @Override
        public long getMaxRate() {
            return 32 * ManaLevel.getLevel(getThis().getBlockType()).getMaxManaContainer();
        }

        /***
         * 添加灵气
         * 返返回加入了多少
         */
        @Override
        public long addMana(long mana) {
            if (mana == 0) {
                return 0;
            }
            long addMana = Math.min(Math.min(mana, this.getMaxRate()), this.getRemainMana());
            this.mana += addMana;
            return addMana;
        }

        /***
         *  抽取灵气
         *  返回提取了多少
         */
        @Override
        public long extractMana(long demand) {
            if (demand == 0) {
                return 0;
            }
            long extractMana = Math.min(Math.min(demand, this.getMaxRate()), this.getMana());
            this.mana -= extractMana;
            return extractMana;
        }

        @Override
        public AllNBT.IGS<NBTBase> getNBTBase() {
            return AllNBT.iManaHandleNBT;
        }

        @Override
        public ManaLevel getManaLevel() {
            return iManaLevel.getManaLevel();
        }

        /***
         * 返回自己
         */
        @Override
        public TileEntity getThis() {
            return tileEntity;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            AllNBT.modMana.set(nbtTagCompound, mana);
            return nbtTagCompound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            mana = AllNBT.modMana.get(nbt);
        }

    }
}
