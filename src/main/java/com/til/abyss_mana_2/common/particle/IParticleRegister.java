package com.til.abyss_mana_2.common.particle;

import com.google.gson.JsonObject;
import com.til.abyss_mana_2.util.Pos;
import com.til.abyss_mana_2.util.extension.List;
import net.minecraft.world.World;


public interface IParticleRegister {

    String type();

    default void add(World world, Pos start, Pos end, JsonObject old) {
        CommonParticle.MAP.get(world, List::new).add(new CommonParticle.Data(type(), start, end, old));
    }


}
