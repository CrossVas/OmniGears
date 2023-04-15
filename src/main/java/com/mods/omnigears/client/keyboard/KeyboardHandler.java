package com.mods.omnigears.client.keyboard;

import com.mods.omnigears.Refs;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class KeyboardHandler {

    @Mod.EventBusSubscriber(modid = Refs.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEventsMod {

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent e) {
            e.register(OmniKeys.MODE_KEY);
            e.register(OmniKeys.TOGGLE_KEY);
            e.register(OmniKeys.ALT_KEY);
            e.register(OmniKeys.BOOST_KEY);

        }
    }

    public static boolean isModeKeyDown() {
        return OmniKeys.MODE_KEY.isDown();
    }

    public static boolean isFlyKeyDown() {
        return OmniKeys.TOGGLE_KEY.isDown();
    }

    public static boolean isBoostKeyDown() {
        return OmniKeys.BOOST_KEY.isDown();
    }

    public static boolean isAltKeyDown() {
        return OmniKeys.ALT_KEY.isDown();
    }

    public static boolean isJumpKeyDown() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }

    public static boolean isForwardKeyDown() {
        return Minecraft.getInstance().options.keyUp.isDown();
    }
}
