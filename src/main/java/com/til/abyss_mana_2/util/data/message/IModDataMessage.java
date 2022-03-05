package com.til.abyss_mana_2.util.data.message;

import com.til.abyss_mana_2.util.data.AllNBT;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public interface IModDataMessage<DATA> {

    void addThis(int id, SimpleNetworkWrapper INSTANCE);

    Side getSide();

    int getID();

    default void upDataToSERVER(DATA data) {
        AllNBT.INSTANCE.sendToServer(this.getData(data));
    }

    default void upDataToPlayerCLIENT(DATA data, EntityPlayerMP player) {
        AllNBT.INSTANCE.sendTo(this.getData(data), player);
    }

    default void upDataToAllPlayerCLIENT(DATA data) {
        AllNBT.INSTANCE.sendToAll(this.getData(data));
    }

    default void upDataToWorldPlayerCLIENT(DATA data, int worldid) {
        AllNBT.INSTANCE.sendToDimension(this.getData(data), worldid);
    }

    default void upDataToTargetPointPlayerCLIENT(DATA data, NetworkRegistry.TargetPoint targetPoint) {
        AllNBT.INSTANCE.sendToAllAround(this.getData(data), targetPoint);
    }

    ModMessage<DATA> getData(DATA data);

    default Object receiveData() {
        return AllNBT.MOD_DATA_GET.get(this);
    }

}
