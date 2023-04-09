package com.mods.omnigears.items.armors.base;

import net.minecraft.world.entity.player.Player;

public interface IProtectionProvider {

    boolean provideProtection();

    boolean isFullSet(Player player);
}
