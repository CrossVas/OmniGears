package com.mods.omnigears.client;

import cofh.lib.api.item.IEnergyContainerItem;
import com.mods.omnigears.items.armors.base.ItemBaseJetpack;
import com.mods.omnigears.utils.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.mods.omnigears.items.armors.base.ItemBaseJetpack.TAG_DISABLED;
import static com.mods.omnigears.items.armors.base.ItemBaseJetpack.TAG_HOVER;

public class JetpackClientHandler {

    public static boolean isFlying(Player player) {
        if (player.isSpectator())
            return false;

        ItemStack armor = player.getInventory().getArmor(2);

        if (armor.getItem() instanceof ItemBaseJetpack) {
            CompoundTag tag = Helpers.getCompoundTag(armor);
            if (!armor.isEmpty()) {
                int energyStorage = ((IEnergyContainerItem) armor.getItem()).getEnergyStored(armor);
                if (energyStorage > 0) {
                    if (tag.getBoolean(TAG_HOVER)) {
                        return !player.isOnGround() && !tag.getBoolean(TAG_DISABLED);
                    } else {
                        return !tag.getBoolean(TAG_DISABLED) && Minecraft.getInstance().options.keyJump.isDown();
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null && !mc.isPaused()) {
            ItemStack chestItem = mc.player.getInventory().getArmor(2);
            if (!chestItem.isEmpty() && chestItem.getItem() instanceof ItemBaseJetpack && isFlying(mc.player)) {
                if (!OmniSound.playing(mc.player.getId())) {
                    mc.getSoundManager().play(new OmniSound(mc.player));
                }
            }
        }
    }
}
