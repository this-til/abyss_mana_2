package com.til.abyss_mana_2.client.util.data.message.player_message;

import com.til.abyss_mana_2.util.data.message.player_message.PlayerMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.text.MessageFormat;

public class PlayerMessage_MessageHandler implements IMessageHandler<PlayerMessage.Message, IMessage> {

    public PlayerMessage_MessageHandler() {
    }

    @Override
    public IMessage onMessage(PlayerMessage.Message message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Object[] objects =  new Object[message.getData().keys.size()];
            for (int i = 0; i < message.getData().keys.size(); i++) {
                objects[i] = I18n.format(message.getData().keys.get(i));
            }
            Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(MessageFormat.format(I18n.format(message.getData().key), objects)), message.getData().actionBar);
        });
        return null;
    }

}
