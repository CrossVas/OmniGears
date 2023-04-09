package com.mods.omnigears.items.armors.base;

import net.minecraft.world.item.ItemStack;

public interface IProtectionProvider {

    int getStoredEnergy(ItemStack stack);
    int getEnergyPerDamage();
    int useEnergy(ItemStack stack, int amount, boolean simulate);
}
