package com.til.abyss_mana_2.common.capability;

import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;


public interface ITileEntityType {

    default Map<Capability<?>, Object> getAllCapabilities(AttachCapabilitiesEvent<TileEntity> event, Map<Capability<?>, Object> map) {
        return map;
    }

}
