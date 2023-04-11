package com.mods.omnigears.items.armors;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mods.omnigears.client.keyboard.KeyboardHandler;
import com.mods.omnigears.items.armors.base.ItemBaseElectricArmor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.UUID;

public class ItemArmorAdvanced extends ItemBaseElectricArmor {

    public EquipmentSlot slot;

    public int energyForExtinguish = 2500;
    public int energyPerBoost = 100;

    public ItemArmorAdvanced(EquipmentSlot slot) {
        super("advanced", slot, 1000000, 5000, Rarity.UNCOMMON);
        this.slot = slot;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ItemArmorAdvanced) {
            if (hasEnergy(stack, this.energyPerBoost)) {
                if (player.isSprinting() || (KeyboardHandler.isForwardKeyDown() && KeyboardHandler.instance.isBoostKeyDown(player))) {
                    if (ticker++ % tickRate == 0) {
                        extractEnergy(stack, this.energyPerBoost, false);
                    }
                    player.moveRelative(player.isInWater() ? 0.1F : player.isCrouching() ? 0.0F : (!player.isOnGround() && !player.isFallFlying() ? 0.025F : 0.22F), new Vec3(0.0, 0.0, 1.0));
                }
            }
        }
        if (!level.isClientSide()) {
            if (ticker++ % tickRate == 0) {
                if (player.isOnFire()) {
                    if (hasEnergy(stack, this.energyForExtinguish) && hasFullSetArmor(player)) {
                        for (int i = 0; i < player.getInventory().items.size(); i++) {
                            player.getInventory().getItem(i);
                            ItemStack mainItem = player.getInventory().getItem(i).copy();
                            if (mainItem.getItem() == Items.ICE) {
                                if (player.getInventory().getItem(i).getCount() >= 1) {
                                    player.getInventory().getItem(i).shrink(1);
                                }
                                extractEnergy(stack, this.energyForExtinguish, false);
                                player.clearFire();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
        UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = ImmutableMultimap.builder();
        if (hasEnergy(stack, this.energyPerDamage)) {
            if (slot == this.slot) {
                modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", getArmorProtection(slot), AttributeModifier.Operation.ADDITION));
                modifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor modifier", 2, AttributeModifier.Operation.ADDITION));

            }
        }
        return modifiers.build();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    public int getArmorProtection(EquipmentSlot slot) {
        switch (slot) {
            case HEAD, FEET -> {
                return 3;
            }
            case LEGS -> {
                return 6;
            }
            case CHEST -> {
                return 8;
            }
        }
        return 0;
    }

    public boolean hasFullSetArmor(Player player) {
        return Arrays.stream(player.getInventory().armor.toArray(new ItemStack[0])).allMatch(item -> item.getItem() instanceof ItemArmorAdvanced);
    }

    @Override
    public boolean isFullSet(Player player) {
        return hasFullSetArmor(player);
    }

    @Override
    public boolean provideProtection() {
        return true;
    }

    // armor boots specific event handler
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onFallEvent(LivingFallEvent e) {
        LivingEntity entity = e.getEntity();
        Level level = entity.level;

        if (!level.isClientSide()) {
            if (entity instanceof Player player) {
                ItemStack feetArmor = player.getItemBySlot(EquipmentSlot.FEET);
                float fallDamage = e.getDistance(); // 100% of fall damage
                if (feetArmor.getItem() instanceof ItemArmorAdvanced) {
                    if (fallDamage > 5) {
                        fallDamage *= 0.25F;
                        int energy = (int) Math.min(fallDamage * energyPerDamage, getEnergyStored(feetArmor));
                        extractEnergy(feetArmor, energy, false);
                        entity.hurt(DamageSource.FALL, fallDamage);
                        e.setCanceled(true);
                    }
                }
            }
        }
    }
}
