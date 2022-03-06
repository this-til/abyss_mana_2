package com.til.abyss_mana_2.common.capability;

import com.google.gson.JsonObject;
import com.til.abyss_mana_2.common.particle.CommonParticle;
import com.til.abyss_mana_2.common.register.BindType;
import com.til.abyss_mana_2.common.register.ManaLevel;
import com.til.abyss_mana_2.util.Pos;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.data.message.player_message.PlayerMessage;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Random;

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
            return 1260000 * getManaLevel().getMaxManaContainer();
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

        public static class WhirlBoostManaHandle extends ManaHandle implements ITickable, IControl {

            public Random random = new Random();

            public long maxRate;
            public final IControl iControl;

            public WhirlBoostManaHandle(TileEntity tileEntity, IManaLevel iManaLevel, IControl iControl) {
                super(tileEntity, iManaLevel);
                this.iControl = iControl;
            }

            @Override
            public void update() {
                extractMana();
            }

            public void extractMana(){
                maxRate = 0;
                Map<TileEntity, IManaHandle> iManaHandles = getCapability(BindType.manaIn);
                iManaHandles.forEach((k, v) -> {
                    if (!(v instanceof WhirlBoostManaHandle))
                        maxRate += v.getMaxRate();
                });
                for (java.util.Map.Entry<TileEntity, IManaHandle> tileEntityIManaHandleEntry : iManaHandles.entrySet()) {
                    long rMana = getRemainMana();
                    if (rMana > 0) {
                        long add;
                        mana += add = tileEntityIManaHandleEntry.getValue().extractMana(rMana);
                        if (random.nextFloat() < add / 320f) {
                            CommonParticle.MANA_TRANSFER.add(getThis().getWorld(), new Pos(tileEntityIManaHandleEntry.getKey().getPos()), new Pos(getThis().getPos()), new JsonObject());
                        }
                    } else {
                        break;
                    }
                }
            }

            /***
             * 返回最大的提取速度
             */
            @Override
            public long getMaxRate() {
                return maxRate;
            }

            /***
             * 返回最大容量
             */
            @Override
            public long getMaxMana() {
                return maxRate * getManaLevel().getMaxManaContainer();
            }

            @Override
            public void unbundlingAll() {
                iControl.unbundlingAll();
            }

            @Override
            public long addMana(long mana) {
                extractMana();
                return super.addMana(mana);
            }

            @Override
            public long extractMana(long demand) {
                extractMana();
                return super.extractMana(demand);
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
    }
}
