package com.til.abyss_mana_2.client.particle;

import com.google.gson.JsonObject;
import com.til.abyss_mana_2.util.Pos;
import net.minecraft.world.World;

public interface IClientParticleRegister {

    String type();

    void run(World world, Pos start, Pos end, JsonObject old);

}
