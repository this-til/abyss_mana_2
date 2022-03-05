package com.til.abyss_mana_2.common;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.capability.AllCapability;
import com.til.abyss_mana_2.common.event.ModEvent;
import com.til.abyss_mana_2.common.other_mod_interact.EventInteractClassTag;
import com.til.abyss_mana_2.common.particle.CommonParticle;
import com.til.abyss_mana_2.common.register.*;
import com.til.abyss_mana_2.util.ClassUtil;
import com.til.abyss_mana_2.util.data.AllNBT;
import com.til.abyss_mana_2.util.data.message.IModDataMessage;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;


@Mod.EventBusSubscriber(modid = AbyssMana2.MODID)
public class CommonProxy {

    public static CommonProxy commonProxy;

    public CommonProxy() {
        commonProxy = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new AllCapability());
        for (Class<?> aClass : ClassUtil.getClasses("com.til.abyss_mana_2.common.other_mod_interact")) {
            EventInteractClassTag subAnnotation = aClass.getAnnotation(EventInteractClassTag.class);
            if (subAnnotation != null) {
                if (Loader.isModLoaded(subAnnotation.modID())) {
                    MinecraftForge.EVENT_BUS.register(aClass);
                }
            }
        }
        AllItem.init();
        OreType.init();
        OreBlock.init();
        OreFluid.init();
        Ore.init();
        ManaLevel.init();
        ManaLevelItem.init();
        ManaLevelBlock.init();
        BindType.init();
        ShapedDrive.init();
        ShapedType.init();
        Shaped.init();
        MinecraftForge.EVENT_BUS.post(new ModEvent.ModEventLoad.preInit());
    }

    public void init(FMLInitializationEvent event) {
        initMessage();
        ManaLevel.initOreDictionary();
        ManaLevel.initRecipe();
        Ore.initOreDictionary();
        Ore.initRecipe();
        MinecraftForge.EVENT_BUS.post(new ModEvent.ModEventLoad.init());
    }

    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.post(new ModEvent.ModEventLoad.postInit());
    }

    public void initMessage() {
        int i = 114514;
        for (IModDataMessage<?> iModDataMessage : AllNBT.MOD_DATA_MESSAGE_LIST) {
            iModDataMessage.addThis(i, AllNBT.INSTANCE);
            AllNBT.MOD_DATA.put(i, iModDataMessage);
            i++;
        }
    }

    public void giveAdvancement(EntityPlayerMP entityPlayerMP, ResourceLocation resourceLocation) {
        Advancement advancement = entityPlayerMP.mcServer.getAdvancementManager().getAdvancement(resourceLocation);
        if (advancement != null) {
            AdvancementProgress advancementProgress = entityPlayerMP.getAdvancements().getProgress(advancement);
            if (!advancementProgress.isDone()) {
                for (String string : advancementProgress.getRemaningCriteria()) {
                    entityPlayerMP.getAdvancements().grantCriterion(advancement, string);
                }
            }
        }
    }

    @SubscribeEvent
    public void doWorldTickEvent(TickEvent.WorldTickEvent event) {

        if (!event.world.isRemote) {
            List<World> removeworld = new List<>();
            for (Map.Entry<World, List<CommonParticle.Data>> worldListEntry : CommonParticle.MAP.entrySet()) {
                if (worldListEntry.getKey() == event.world) {
                    for (CommonParticle.Data data : worldListEntry.getValue()) {
                        AllNBT.particleMessage.upDataToWorldPlayerCLIENT(data.getJson().toString(), event.world.provider.getDimension());
                    }
                    worldListEntry.getValue().clear();
                    removeworld.add(worldListEntry.getKey());
                }
            }
            for (World world : removeworld) {
                CommonParticle.MAP.remove(world);
            }
        }
    }

    @SubscribeEvent
    public void onEvent(RegistryEvent.NewRegistry event) {
        ManaLevel.register = new RegistryBuilder<ManaLevel>().setName(new ResourceLocation(AbyssMana2.MODID, "mana_level_z")).setType(ManaLevel.class).create();
        ManaLevelItem.register = new RegistryBuilder<ManaLevelItem>().setName(new ResourceLocation(AbyssMana2.MODID, "mana_level_item_map")).setType(ManaLevelItem.class).create();
        ManaLevelBlock.register = new RegistryBuilder<ManaLevelBlock>().setName(new ResourceLocation(AbyssMana2.MODID, "mana_level_block_map")).setType(ManaLevelBlock.class).create();
        BindType.register = new RegistryBuilder<BindType>().setName(new ResourceLocation(AbyssMana2.MODID, "bund_type_map")).setType(BindType.class).create();
        Shaped.register = new RegistryBuilder<Shaped>().setName(new ResourceLocation(AbyssMana2.MODID, "shaped_z")).setType(Shaped.class).addCallback((IForgeRegistry.AddCallback<Shaped>) (owner, stage, id, shaped, oldObj) -> {
            Shaped.map.get(shaped.shapedType, Map::new).get(shaped.shapedDrive, List::new).add(shaped);
        }).create();
        ShapedDrive.register = new RegistryBuilder<ShapedDrive>().setName(new ResourceLocation(AbyssMana2.MODID, "shaped_provide_map")).setType(ShapedDrive.class).create();
        ShapedType.register = new RegistryBuilder<ShapedType>().setName(new ResourceLocation(AbyssMana2.MODID, "shaped_Type_map")).setType(ShapedType.class).create();
        OreType.register = new RegistryBuilder<OreType>().setName(new ResourceLocation(AbyssMana2.MODID, "ore_type_map")).setType(OreType.class).create();
        OreBlock.register = new RegistryBuilder<OreBlock>().setName(new ResourceLocation(AbyssMana2.MODID, "ore_mineral_map")).setType(OreBlock.class).create();
        OreFluid.register = new RegistryBuilder<OreFluid>().setName(new ResourceLocation(AbyssMana2.MODID, "ore_fluid_map")).setType(OreFluid.class).create();
        Ore.register = new RegistryBuilder<Ore>().setName(new ResourceLocation(AbyssMana2.MODID, "ore_z")).setType(Ore.class).create();
    }

}
