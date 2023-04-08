package com.mods.omnigears.items.armors.base;

import com.mods.omnigears.Refs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class ArmorMaterialOmni implements ArmorMaterial {

    public String id;

    public ArmorMaterialOmni(String id) {
        this.id = id;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return 0;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return 0;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return Refs.ID + ":" + this.id;
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
