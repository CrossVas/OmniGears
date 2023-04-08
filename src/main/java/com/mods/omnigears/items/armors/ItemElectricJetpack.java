package com.mods.omnigears.items.armors;

import com.mods.omnigears.items.armors.base.ItemBaseJetpack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class ItemElectricJetpack extends ItemBaseJetpack {

    boolean isBoostProvider, isEnergyProvider;

    public ItemElectricJetpack(String id, int capacity, int transfer, Rarity rarity, int usage, boolean isBoostProvider, boolean isEnergyProvider) {
        super(id, capacity, transfer, rarity);
        this.usage = usage;
        this.isBoostProvider = isBoostProvider;
        this.isEnergyProvider = isEnergyProvider;
    }

    @Override
    public boolean canProvideBoost(ItemStack stack) {
        return this.isBoostProvider;
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return this.isEnergyProvider;
    }
}
