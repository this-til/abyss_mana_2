package com.til.abyss_mana_2.util.data.message.player_message;

import com.til.abyss_mana_2.client.util.data.message.player_message.PlayerMessage_MessageHandler;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.data.message.IModDataMessage;
import com.til.abyss_mana_2.util.data.message.ModMessage;
import com.til.abyss_mana_2.util.extension.List;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;

public class PlayerMessage implements IModDataMessage<PlayerMessage.MessageData> {

    public int id;

    public PlayerMessage() {
        AllNBT.MOD_DATA_MESSAGE_LIST.add(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void addThis(int id, SimpleNetworkWrapper INSTANCE) {
        INSTANCE.registerMessage(PlayerMessage_MessageHandler.class, Message.class, id, this.getSide());
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
    public ModMessage<MessageData> getData(MessageData o) {
        return new Message(this, o);
    }

    public static class Message extends ModMessage<MessageData> {

        public Message() {
        }

        public Message(IModDataMessage<?> iModDataMessage, MessageData o) {
            super(iModDataMessage, o);
        }

        @Override
        public void dataToBytes(ByteBuf buf) {
            char[] chars = getData().key.toCharArray();
            buf.writeInt(chars.length);
            for (char aChar : chars) {
                buf.writeChar(aChar);
            }
            buf.writeInt(getData().keys.size());
            for (String key : getData().keys) {
                char[] _chars = key.toCharArray();
                buf.writeInt(_chars.length);
                for (char aChar : _chars) {
                    buf.writeChar(aChar);
                }
            }
            buf.writeBoolean(getData().actionBar);
        }

        @Override
        public MessageData dataFromBytes(ByteBuf buf) {
            int i = buf.readInt();
            char[] chars = new char[i];
            for (int i1 = 0; i1 < i; i1++) {
                chars[i1] = buf.readChar();
            }
            String key = new String(chars);
            int keys_l = buf.readInt();
            String[] stringList = new String[keys_l];
            for (int i1 = 0; i1 < keys_l; i1++) {
                int key_e_l = buf.readInt();
                char[] chars1 = new char[key_e_l];
                for (int i2 = 0; i2 < key_e_l; i2++) {
                    chars1[i2] = buf.readChar();
                }
                stringList[i1] = new String(chars1);
            }
            return new MessageData(buf.readBoolean(), key, stringList);
        }
    }

    public static class MessageData {
        public String key;
        public List<String> keys;
        public boolean actionBar;

        public MessageData(boolean actionBar, String key, String... strings) {
            this.key = key;
            keys = new List<>();
            if (strings != null)
                keys.addAll(Arrays.asList(strings));
            this.actionBar = actionBar;
        }
    }

}
