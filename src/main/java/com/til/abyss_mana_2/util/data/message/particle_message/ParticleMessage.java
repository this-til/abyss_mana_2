package com.til.abyss_mana_2.util.data.message.particle_message;

import com.til.abyss_mana_2.client.util.data.message.particle_message.ParticleMessage_MessageHandler;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.data.message.IModDataMessage;
import com.til.abyss_mana_2.util.data.message.ModMessage;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ParticleMessage implements IModDataMessage<String> {

    public int id;

    public ParticleMessage() {
        AllNBT.MOD_DATA_MESSAGE_LIST.add(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void addThis(int id, SimpleNetworkWrapper INSTANCE) {
        INSTANCE.registerMessage(ParticleMessage_MessageHandler.class, Message.class, id, this.getSide());
        this.id = id;
        AllNBT.MOD_DATA.put(id, this);
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public ModMessage<String> getData(String o) {
        return new Message(this, o);
    }

    public static class Message extends ModMessage<String> {

        public Message() {
        }

        public Message(IModDataMessage<?> iModDataMessage, String o) {
            super(iModDataMessage, o);
        }

        @Override
        public void dataToBytes(ByteBuf buf) {
            char[] chars = getData().toCharArray();
            buf.writeInt(chars.length);
            for (char aChar : chars) {
                buf.writeChar(aChar);
            }
        }

        @Override
        public String dataFromBytes(ByteBuf buf) {
            int i = buf.readInt();
            char[] chars = new char[i];
            for (int i1 = 0; i1 < i; i1++) {
                chars[i1] = buf.readChar();
            }
            return new String(chars);
        }
    }

}
