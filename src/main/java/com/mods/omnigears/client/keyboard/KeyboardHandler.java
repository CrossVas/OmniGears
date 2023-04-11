package com.mods.omnigears.client.keyboard;

import com.mods.omnigears.Refs;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
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

    public static KeyboardHandler instance = new KeyboardHandler();

    public boolean isModeSwitchKeyDown(Player player) {
        return this.getHandler(player).isKeyPressed(OmniKeys.MODE_KEY);
    }

    public boolean isToggleKeyDown(Player player) {
        return this.getHandler(player).isKeyPressed(OmniKeys.TOGGLE_KEY);
    }

    public boolean isAltKeyDown(Player player) {
        return this.getHandler(player).isKeyPressed(OmniKeys.ALT_KEY);
    }

    public boolean isBoostKeyDown(Player player) {
        return this.getHandler(player).isKeyPressed(OmniKeys.BOOST_KEY);
    }

    public static boolean isJumpKeyDown() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }

    public static boolean isForwardKeyDown() {
        return Minecraft.getInstance().options.keyUp.isDown();
    }

    public PlayerHandler getHandler(Player player) {
        return PlayerHandler.getHandler(player);
    }
}
