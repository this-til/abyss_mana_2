package com.til.abyss_mana_2.util.data.message;

import com.til.abyss_mana_2.util.data.AllNBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ModMessageHandler<REQ extends ModMessage<?>> implements IMessageHandler<REQ, IMessage> {

    @Override
    public IMessage onMessage(REQ message, MessageContext ctx) {
        AllNBT.MOD_DATA_GET.put(message.getiModDataMessage(), message.getData());
        return null;
    }
}
