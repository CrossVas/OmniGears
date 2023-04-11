package com.mods.omnigears.client.keyboard;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.UUID;

public class PlayerHandler {

    static Map<UUID, PlayerHandler> INSTANCES = new Object2ObjectOpenHashMap<>();
    static PlayerHandler CLIENT_INSTANCE = new PlayerHandler();
    Player player;

    public PlayerHandler() {

    }

    public static boolean isKeyDown(KeyMapping key) {
        if (key.isUnbound()) return false;
        InputConstants.Key input = key.getKey();
        long monitor = Minecraft.getInstance().getWindow().getWindow();
        return input.getType() == InputConstants.Type.MOUSE ? GLFW.glfwGetMouseButton(monitor, input.getValue()) == 1: InputConstants.isKeyDown(monitor, input.getValue());
    }

    public boolean isKeyPressed(KeyMapping binding) {
        IKeyConflictContext context = binding.getKeyConflictContext();
        binding.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
        boolean result = isKeyDown(binding);
        binding.setKeyConflictContext(context);
        return result;
    }

    public static PlayerHandler getHandler(Player player) {
        if (EffectiveSide.get().isClient()) {
            return getClientPlayerHandler(player);
        } else {
            PlayerHandler handler = INSTANCES.get(player.getUUID());
            if (handler == null) {
                handler = new PlayerHandler();
                INSTANCES.put(player.getUUID(), handler);
            }

            handler.player = player;
            return handler;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static PlayerHandler getClientPlayerHandler(Player player) {
        if (player != Minecraft.getInstance().player && player != null) {
            PlayerHandler handler = INSTANCES.get(player.getUUID());
            if (handler == null) {
                handler = new PlayerHandler();
                INSTANCES.put(player.getUUID(), handler);
            }

            handler.player = player;
            return handler;
        } else {
            return getClientHandler();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static PlayerHandler getClientHandler() {
        CLIENT_INSTANCE.player = Minecraft.getInstance().player;
        return CLIENT_INSTANCE;
    }
}
