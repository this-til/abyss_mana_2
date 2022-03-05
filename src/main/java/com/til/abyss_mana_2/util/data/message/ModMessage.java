package com.til.abyss_mana_2.util.data.message;

import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.data.message.IModDataMessage;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class ModMessage<DATA> implements IMessage {

    private DATA data;
    private IModDataMessage<?> iModDataMessage;

    public ModMessage() {
    }

    public ModMessage(IModDataMessage<?> iModDataMessage, DATA data) {
        this.data = data;
        this.iModDataMessage = iModDataMessage;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.iModDataMessage = AllNBT.MOD_DATA.get(buf.readInt());
        this.data = this.dataFromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(getiModDataMessage().getID());
        this.dataToBytes(buf);
    }

    public abstract void dataToBytes(ByteBuf buf);

    public abstract DATA dataFromBytes(ByteBuf buf);

    public IModDataMessage<?> getiModDataMessage() {
        return iModDataMessage;
    }

    public DATA getData() {
        return this.data;
    }

}
