package com.til.abyss_mana_2;

import com.til.abyss_mana_2.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = AbyssMana2.MODID, name = AbyssMana2.NAME, version = AbyssMana2.VERSION)
public class AbyssMana2
{
    public static final String MODID = "abyss_mana_2";
    public static final String NAME = "abyss_mana_2";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @SidedProxy(clientSide = "com.til.abyss_mana_2.client.ClientProxy", serverSide = "com.til.abyss_mana_2.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(AbyssMana2.MODID)
    public static AbyssMana2 instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
