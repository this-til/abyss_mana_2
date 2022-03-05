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
    int getMaxMana();

    /***
     * 返回当前容量
     */
    int getMana();

    /***
     * 返回剩余空间
     */
    default int getRemainMana() {
        return Math.max(this.getMaxMana() - this.getMana(), 0);
    }

    /***
     * 返回最大的提取速度
     */
    int getMaxRate();

    /***
     * 添加灵气
     * 返返回加入了多少
     */
    int addMana(int mana);

    /***
     *  抽取灵气
     *  返回提取了多少
     */
    int extractMana(int demand);

    class ManaHandle implements IManaHandle {

        public int mana;
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
        public int getMaxMana() {
            return 160000 * getManaLevel().manaLevelData.getMaxManaContainer();
        }

        /***
         * 返回当前容量
         */
        @Override
        public int getMana() {
            return mana;
        }

        /***
         * 返回最大的提取速度
         */
        @Override
        public int getMaxRate() {
            return 32 * ManaLevel.getLevel(getThis().getBlockType()).manaLevelData.getMaxManaContainer();
        }

        /***
         * 添加灵气
         * 返返回加入了多少
         */
        @Override
        public int addMana(int mana) {
            if (mana == 0) {
                return 0;
            }
            int addMana = Math.min(Math.min(mana, this.getMaxRate()), this.getRemainMana());
            this.mana += addMana;
            return addMana;
        }

        /***
         *  抽取灵气
         *  返回提取了多少
         */
        @Override
        public int extractMana(int demand) {
            if (demand == 0) {
                return 0;
            }
            int extractMana = Math.min(Math.min(demand, this.getMaxRate()), this.getMana());
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
