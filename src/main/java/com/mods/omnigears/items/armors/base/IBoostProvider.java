package com.mods.omnigears.items.armors.base;

import net.minecraft.world.item.ItemStack;

public interface IBoostProvider {

    boolean canProvideBoost(ItemStack stack);
}
