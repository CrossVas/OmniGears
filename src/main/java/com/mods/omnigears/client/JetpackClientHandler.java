package com.mods.omnigears.client;

import com.mods.omnigears.Helpers;
import com.mods.omnigears.items.armors.ItemAdvancedOmniArmor;
import com.mods.omnigears.items.armors.base.ItemBaseJetpack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JetpackClientHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null && !mc.isPaused()) {
            ItemStack chestItem = mc.player.getItemBySlot(EquipmentSlot.CHEST);
            if (!chestItem.isEmpty() && Helpers.isFlying(mc.player)) {
                if (!OmniSound.playing(mc.player.getId())) {
                    if (chestItem.getItem() instanceof ItemBaseJetpack) {
                        mc.getSoundManager().play(new OmniSound(mc.player, OmniSounds.JETPACK_SOUND.get()));
                    } else if (chestItem.getItem() instanceof ItemAdvancedOmniArmor) {
                        mc.getSoundManager().play(new OmniSound(mc.player, OmniSounds.OMNI_SOUND.get()));
                    }
                }
            }
        }
    }
}
