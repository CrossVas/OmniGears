package com.mods.omnigears.items.armors;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mods.omnigears.items.armors.base.IProtectionProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.UUID;
import java.util.function.Consumer;

public class ItemAdvancedNanoChest extends ItemElectricJetpack implements IProtectionProvider {

    public int energyPerDamage = 3200;

    public ItemAdvancedNanoChest() {
        super("advanced_nano", 4000000, 5000, Rarity.UNCOMMON, 144, true, true);
    }

    @Override
    public int getStoredEnergy(ItemStack stack) {
        return getEnergyStored(stack);
    }

    @Override
    public int getEnergyPerDamage() {
        return energyPerDamage;
    }

    @Override
    public int useEnergy(ItemStack stack, int amount, boolean simulate) {
        return extractEnergy(stack, amount, simulate);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
        UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = ImmutableMultimap.builder();
        if (slot == EquipmentSlot.CHEST) {
            modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", 8, AttributeModifier.Operation.ADDITION));
            modifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor modifier", 2, AttributeModifier.Operation.ADDITION));
        }
        return modifiers.build();
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        extractEnergy(stack, amount * energyPerDamage, false);
        return 0;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        setEnergyStored(stack, getEnergyStored(stack) - energyPerDamage * damage);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
}
