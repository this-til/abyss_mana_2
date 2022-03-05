package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class OreFluid extends RegisterBasics<OreFluid> {

    public static IForgeRegistry<OreFluid> register = null;

    public OreFluid(String name) {
        this(new ResourceLocation(AbyssMana2.MODID, name));
    }

    public OreFluid(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public Fluid getFluid(Ore ore) {
        String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
        return new Fluid(name, new ResourceLocation(AbyssMana2.MODID, "block/solution"), new ResourceLocation(AbyssMana2.MODID, "block/solution"), ore.color)
                .setLuminosity(0)
                .setDensity(1000)
                .setViscosity(1000)
                .setGaseous(false);
    }

    public int getLayer() {
        return 0;
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<OreFluid> event) {
        event.getRegistry().register(this);
    }

    /***
     * 普通溶液
     */
    public static OreFluid solution;

    /***
     * 溶解灵气
     */
    public static OreFluid manaSolution;

    /***
     * 等离子
     */
    public static OreFluid plasmaSolution;

    public static void init() {
        solution = new OreFluid("solution");
        manaSolution = new OreFluid("mana_solution") {
            @Override
            public Fluid getFluid(Ore ore) {
                return super.getFluid(ore).setLuminosity(6);
            }
        };
        plasmaSolution = new OreFluid("plasma_solution") {
            @Override
            public Fluid getFluid(Ore ore) {
                String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
                return new Fluid(name, new ResourceLocation(AbyssMana2.MODID, "block/plasma_solution"), new ResourceLocation(AbyssMana2.MODID, "block/plasma_solution"), ore.color)
                        .setLuminosity(15)
                        .setDensity(1000)
                        .setViscosity(5000)
                        .setGaseous(false);
            }
        };
    }
}
