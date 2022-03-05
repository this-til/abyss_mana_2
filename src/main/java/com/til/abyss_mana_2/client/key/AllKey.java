package com.til.abyss_mana_2.client.key;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.key.KeyRun;
import com.til.abyss_mana_2.util.data.AllNBT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class AllKey {

    public static KeyBinding key_I;

    public static void init() {
        key_I = new KeyBinding(AbyssMana2.MODID + ".i.key", Keyboard.KEY_G, AbyssMana2.MODID + ".key"){
            @SubscribeEvent
            public void onKeyInput(InputEvent.KeyInputEvent event) {
                if (Minecraft.getMinecraft().currentScreen == null) {
                    if (Keyboard.isKeyDown(this.getKeyCode())) {
                        AllNBT.keyMessage.upDataToSERVER(KeyRun.key_G.name());
                    }
                }
            }
        };
        ClientRegistry.registerKeyBinding(key_I);
        MinecraftForge.EVENT_BUS.register(key_I);
    }

}
