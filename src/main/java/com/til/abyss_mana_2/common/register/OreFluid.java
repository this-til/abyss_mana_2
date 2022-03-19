package com.til.abyss_mana_2.common.register;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.util.extension.GenericParadigmMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.awt.*;
import java.util.Objects;

public class OreFluid extends RegisterBasics<OreFluid> {

    public static IForgeRegistry<OreFluid> register = null;

    public OreFluid(String name, GenericParadigmMap genericParadigmMap) {
        this(new ResourceLocation(AbyssMana2.MODID, name), genericParadigmMap);
    }

    public OreFluid(ResourceLocation resourceLocation, GenericParadigmMap genericParadigmMap) {
        super(resourceLocation, genericParadigmMap);
    }

    public Fluid getFluid(Ore ore) {
        String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
        return new Fluid(name, new ResourceLocation(AbyssMana2.MODID, "block/solution"), new ResourceLocation(AbyssMana2.MODID, "block/solution"), ore.getGenericParadigmMap().get(Ore.color))
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

    /***
     * 充能红石溶液
     */
    public static OreFluid chargingRedstoneSolution;

    public static OreFluid uuSolution;

    public static void init() {
        solution = new OreFluid("solution", new GenericParadigmMap());
        manaSolution = new OreFluid("mana_solution", new GenericParadigmMap()) {
            @Override
            public Fluid getFluid(Ore ore) {
                return super.getFluid(ore).setLuminosity(6);
            }
        };
        plasmaSolution = new OreFluid("plasma_solution", new GenericParadigmMap()) {
            @Override
            public Fluid getFluid(Ore ore) {
                String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
                return new Fluid(name, new ResourceLocation(AbyssMana2.MODID, "block/plasma_solution"), new ResourceLocation(AbyssMana2.MODID, "block/plasma_solution"), ore.getGenericParadigmMap().get(Ore.color))
                        .setLuminosity(15)
                        .setDensity(1000)
                        .setViscosity(5000)
                        .setGaseous(false);
            }
        };
        chargingRedstoneSolution = new OreFluid("charging_redstone_solution", new GenericParadigmMap()) {
            @Override
            public Fluid getFluid(Ore ore) {
                Color color = ore.getGenericParadigmMap().get(Ore.color);
                String name = Objects.requireNonNull(getRegistryName()).getResourcePath() + "_" + Objects.requireNonNull(ore.getRegistryName()).getResourcePath();
                return new Fluid(name, new ResourceLocation(AbyssMana2.MODID, "block/solution"), new ResourceLocation(AbyssMana2.MODID, "block/solution"), color)
                        .setLuminosity(15)
                        .setDensity(1000)
                        .setViscosity(3000)
                        .setGaseous(false);
            }
        };
        uuSolution = new OreFluid("uu_solution", new GenericParadigmMap());

    }

}
