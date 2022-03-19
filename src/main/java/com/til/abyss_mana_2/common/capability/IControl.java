package com.til.abyss_mana_2.common.capability;

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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Objects;


public interface IControl extends INBT, IThis<TileEntity>, IManaLevel {

    /***
     * 全部解绑
     */
    void unbundlingAll();

    /***
     * 绑定
     */
    PlayerMessage.MessageData binding(TileEntity tileEntity, BindType iBindType);

    /***
     * 解绑
     */
    PlayerMessage.MessageData unBindling(TileEntity tileEntity, BindType iBindType);

    boolean hasBundling(TileEntity tileEntity, BindType bindType);

    List<TileEntity> getAllTileEntity(BindType iBindType);

    <C> Map<TileEntity, C> getCapability(Capability<C> capability, BindType iBindType);

    <C> Map<TileEntity, C> getCapability(BindType.BundTypeBindCapability<C> bundTypeBindCapability);

    /***
     * 获取可以绑定实体方块的最大范围
     */
    int getMaxRange();

    /***
     * 获取最大绑定数量
     */
    int getMaxBind();

    /***
     * 获得可以绑定的类型
     */
    List<BindType> getCanBindType();

    class Control implements IControl {

        public final TileEntity tileEntity;
        public final IManaLevel iManaLevel;
        public List<BindType> bindTypes;
        public Map<BindType, List<BlockPos>> tile = new Map<>();

        public Control(TileEntity tileEntity,List<BindType> bindTypes, IManaLevel iManaLevel) {
            this.tileEntity = tileEntity;
            this.iManaLevel = iManaLevel;
            this.bindTypes = bindTypes;
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
            return AllNBT.iControlNBT;
        }

        @Override
        public List<TileEntity> getAllTileEntity(BindType iBindType) {
            List<TileEntity> list = new List<>();
            List<BlockPos> rList = new List<>();
            List<BlockPos> blockPosList = tile.get(iBindType, List::new);
            for (BlockPos blockPos : blockPosList) {
                TileEntity tile = getThis().getWorld().getTileEntity(blockPos);
                if (tile == null) {
                    rList.add(blockPos);
                } else {
                    list.add(tile);
                }
            }
            for (BlockPos blockPos : rList) {
                blockPosList.remove(blockPos);
            }
            return list;
        }

        @Override
        public PlayerMessage.MessageData binding(TileEntity tileEntity, BindType iBindType) {
            List<BlockPos> list = tile.get(iBindType, List::new);
            if (tileEntity.getWorld() != tileEntity.getWorld()) {
                return new PlayerMessage.MessageData(true, "错误，方块不属于同一个世界.name");
            }
            if (!getCanBindType().contains(iBindType)) {
                return new PlayerMessage.MessageData(true, "绑定失败，不支持类型为{0}的绑定", Objects.requireNonNull(iBindType.getRegistryName()).toString());
            }
            if (new Pos(tileEntity.getPos()).getDistance(new Pos(tileEntity.getPos())) > getMaxRange()) {
                return new PlayerMessage.MessageData(true, "绑定失败，方块距离超过限制.name");
            }
            if (list.size() >= getMaxBind()) {
                return new PlayerMessage.MessageData(true, "绑定失败，已达到绑定类型{0}的最大绑定数量.name", Objects.requireNonNull(iBindType.getRegistryName()).toString());
            }
            if (this.getAllTileEntity(iBindType).contains(tileEntity)) {
                return new PlayerMessage.MessageData(true, "绑定失败，该方块已经被绑定过了.name");
            }
            list.add(tileEntity.getPos());
            return new PlayerMessage.MessageData(true, "绑定成功.name");
        }

        @Override
        public boolean hasBundling(TileEntity tileEntity, BindType bindType) {
            return tile.get(bindType, List::new).contains(tileEntity.getPos());
        }

        @Override
        public PlayerMessage.MessageData unBindling(TileEntity tileEntity, BindType iBindType) {
            if (tileEntity.getWorld() != tileEntity.getWorld()) {
                return new PlayerMessage.MessageData(true, "错误，方块不属于同一个世界.name");
            }
            if (!this.getAllTileEntity(iBindType).contains(tileEntity)) {
                return new PlayerMessage.MessageData(true, "解绑失败，方块没有被绑定.name");
            }
            tile.get(iBindType, List::new).remove(tileEntity.getPos());
            return new PlayerMessage.MessageData(true, "解绑成功.name");
        }

        @Override
        public void unbundlingAll() {
            tile.clear();
        }

        @Override
        public <C> Map<TileEntity, C> getCapability(Capability<C> capability, BindType iBindType) {
            Map<TileEntity, C> map = new Map<>();
            for (TileEntity entity : getAllTileEntity(iBindType)) {
                C c = entity.getCapability(capability, null);
                if (c != null) {
                    map.put(entity, c);
                }
            }
            return map;
        }

        @Override
        public <C> Map<TileEntity, C> getCapability(BindType.BundTypeBindCapability<C> bundTypeBindCapability) {
            return getCapability(bundTypeBindCapability.getCapability(), bundTypeBindCapability);
        }

        @Override
        public ManaLevel getManaLevel() {
            return iManaLevel.getManaLevel();
        }

        /***
         * 获取可以绑定实体方块的最大范围
         */
        @Override
        public int getMaxRange() {
            return 16;
        }

        /***
         * 获取最大绑定数量
         */
        @Override
        public int getMaxBind() {
            return 2 + getManaLevel().getGenericParadigmMap().get(ManaLevel.level) / 2;
        }

        /***
         * 获得可以绑定的类型
         */
        @Override
        public List<BindType> getCanBindType() {
            return bindTypes;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            tile = AllNBT.controlBindBlock.get(nbt);
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            AllNBT.controlBindBlock.set(nbtTagCompound, tile);
            return nbtTagCompound;
        }
    }
}
