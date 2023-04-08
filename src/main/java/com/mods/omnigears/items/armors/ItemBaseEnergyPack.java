package com.mods.omnigears.items.armors;

import com.mods.omnigears.items.armors.base.IEnergyPack;
import com.mods.omnigears.items.armors.base.ItemBaseElectricArmor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class ItemBaseEnergyPack extends ItemBaseElectricArmor implements IEnergyPack {

    public int capacity, transfer;

    public ItemBaseEnergyPack(String id, int capacity, int transfer, Rarity rarity) {
        super(id, EquipmentSlot.CHEST, capacity, transfer, rarity);
        this.capacity = capacity;
        this.transfer = transfer;
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return true;
    }
}
