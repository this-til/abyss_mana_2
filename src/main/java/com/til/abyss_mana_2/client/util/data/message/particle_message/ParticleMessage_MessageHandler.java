package com.til.abyss_mana_2.client.util.data.message.particle_message;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.til.abyss_mana_2.client.particle.ClientParticleRegister;
import com.til.abyss_mana_2.common.particle.CommonParticle;
import com.til.abyss_mana_2.util.data.message.particle_message.ParticleMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ParticleMessage_MessageHandler implements IMessageHandler<ParticleMessage.Message, IMessage> {

    public ParticleMessage_MessageHandler() {
    }

    @Override
    public IMessage onMessage(ParticleMessage.Message message, MessageContext ctx) {
        JsonObject jsonObject = new JsonParser().parse(message.getData()).getAsJsonObject();
        ClientParticleRegister.run(new CommonParticle.Data(jsonObject));
        return null;
    }
}
