package com.til.abyss_mana_2.client.particle;

import com.google.gson.JsonObject;
import com.til.abyss_mana_2.common.particle.CommonParticle;
import com.til.abyss_mana_2.util.Pos;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public enum ClientParticleRegister implements IClientParticleRegister {

    AIR {
        @Override
        public void run(World world, Pos start, Pos end, JsonObject old) {

        }
    },
    MANA_TRANSFER {
        @Override
        public void run(World world, Pos start, Pos end, JsonObject old) {
            Pos r = Pos.getRandomPos();
            end.toMove(r);

            int dis = (int) start.getDistance(end) * 3;
            Pos direction = Pos.getMovePos(start, end, dis);

            Minecraft.getMinecraft().effectRenderer.addEffect(new DefaultParticle(world, start.getX(), start.getY(), start.getZ(), 0, 0, 0) {

                @Override
                public Pos getRBG() {
                    if (old.has("rgb")) {
                        return new Pos(old.getAsJsonObject("rgb"));
                    }
                    return new Pos(0, 1, 1);
                }

                @Override
                public Pos getMove() {
                    return direction;
                }

                @Override
                public int getMaxAge() {
                    return dis;
                }

                @Override
                public float getMaxScale() {
                    return 0.25f;
                }
            });
        }
    },
    ITEM_TRANSFER {
        @Override
        public void run(World world, Pos start, Pos end, JsonObject old) {
            {
                int dis = (int) start.getDistance(end) * 6;
                Pos direction = Pos.getMovePos(start, end, dis);

                Minecraft.getMinecraft().effectRenderer.addEffect(new DefaultParticle(world, start.getX(), start.getY(), start.getZ(), 0, 0, 0) {
                    @Override
                    public Pos getMove() {
                        return direction;
                    }

                    @Override
                    public float getMaxScale() {
                        return 1.5f;
                    }

                    @Override
                    public int getMaxAge() {
                        return dis;
                    }

                    @Override
                    public Pos getRBG() {
                        if (old.has("rgb")) {
                            return new Pos(old.getAsJsonObject("rgb"));
                        }
                        return new Pos(0, 1, 0);
                    }
                });
            }

            for (int i = 0; i < 15; i++) {

                Pos p = Pos.getRandomPos(1.5, 1.5, 1.5);
                Pos e = new Pos(p.getX() + end.getX(), p.getY() + end.getY(), p.getZ() + end.getZ());
                int dis = (int) start.getDistance(e) * 6;
                Pos direction = Pos.getMovePos(start, e, dis);

                Minecraft.getMinecraft().effectRenderer.addEffect(new DefaultParticle(world, start.getX(), start.getY(), start.getZ(), 0, 0, 0) {
                    @Override
                    public Pos getMove() {
                        return direction;
                    }

                    @Override
                    public float getMaxScale() {
                        return 0.25f;
                    }

                    @Override
                    public int getMaxAge() {
                        return dis;
                    }

                    @Override
                    public Pos getRBG() {
                        if (old.has("rgb_old")) {
                            return new Pos(old.getAsJsonObject("rgb_old"));
                        }
                        return new Pos(0, 1, 0);
                    }
                    
                });
            }
        }
    },
    FLUID_TRANSFER{
        @Override
        public void run(World world, Pos start, Pos end, JsonObject old) {
            Pos r = Pos.getRandomPos();
            end.toMove(r);

            int dis = (int) start.getDistance(end) * 6;
            Pos direction = Pos.getMovePos(start, end, dis);

            Minecraft.getMinecraft().effectRenderer.addEffect(new DefaultParticle(world, start.getX(), start.getY(), start.getZ(), 0, 0, 0) {

                @Override
                public Pos getRBG() {
                    if (old.has("rgb")) {
                        return new Pos(old.getAsJsonObject("rgb"));
                    }
                    return new Pos(0, 0, 1);
                }

                @Override
                public Pos getMove() {
                    return direction;
                }

                @Override
                public int getMaxAge() {
                    return dis;
                }

                @Override
                public float getMaxScale() {
                    return 0.25f;
                }
            });
        }
    };

    @Override
    public String type() {
        return toString();
    }

    public static final Map<String, IClientParticleRegister> map = new HashMap<>();

    static {
        for (ClientParticleRegister value : values()) {
            map.put(value.toString(), value);
        }
    }

    public static void run(CommonParticle.Data data) {
        IClientParticleRegister iClientParticleRegister = map.get(data.type);
        if (iClientParticleRegister != null) {
            iClientParticleRegister.run(Minecraft.getMinecraft().world, data.start, data.end, data.old);
        }
    }
}
