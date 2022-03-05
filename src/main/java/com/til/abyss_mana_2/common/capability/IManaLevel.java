package com.til.abyss_mana_2.common.capability;

import com.til.abyss_mana_2.common.register.ManaLevel;
import net.minecraft.tileentity.TileEntity;

public interface IManaLevel extends IThis<TileEntity> {

    ManaLevel getManaLevel();

    class GetManaLevel implements IManaLevel {

        public final TileEntity tileEntity;

        public GetManaLevel(TileEntity tileEntity) {
            this.tileEntity = tileEntity;
        }

        @Override
        public ManaLevel getManaLevel() {
            return ManaLevel.getLevel(getThis().getBlockType());
        }

        /***
         * 返回自己
         */
        @Override
        public TileEntity getThis() {
            return tileEntity;
        }
    }

}
