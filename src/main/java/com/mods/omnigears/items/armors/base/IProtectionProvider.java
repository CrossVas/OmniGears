package com.mods.omnigears.items.armors.base;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IProtectionProvider {

    boolean provideProtection();

    boolean isFullSet(Player player);

    void useEnergy(ItemStack stack, int amount);
}
