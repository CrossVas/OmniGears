package com.mods.omnigears.client;

import com.mods.omnigears.Refs;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class Keyboard {

    public static KeyMapping MODE_KEY = new KeyMapping("key.mode_switch.name", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, Refs.NAME);
    public static KeyMapping TOGGLE_KEY = new KeyMapping("key.toggle.name", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F, Refs.NAME);
    public static KeyMapping ALT_KEY = new KeyMapping("key.alt.name", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, Refs.NAME);
    public static KeyMapping BOOST_KEY = new KeyMapping("key.boost.name", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_TAB, Refs.NAME);

    public static boolean isModeSwitchKeyDown() {
        return MODE_KEY.isDown();
    }

    public static boolean isFlyKeyDown() {
        return TOGGLE_KEY.isDown();
    }

    public static boolean isAltKeyDown() {
        return ALT_KEY.isDown();
    }

    public static boolean isBoostKeyDown() {
        return BOOST_KEY.isDown();
    }
}
