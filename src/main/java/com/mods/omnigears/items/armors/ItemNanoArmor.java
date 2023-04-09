package com.mods.omnigears.items.armors;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mods.omnigears.items.armors.base.IProtectionProvider;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class ItemNanoArmor extends ItemBaseElectricArmor implements IProtectionProvider {

    public int energyPerDamage = 3200;
    public EquipmentSlot slot;
    public byte ticker, tickRate = 20;
    public int energyForExtinguish = 25000;

    public ItemNanoArmor(String id, EquipmentSlot slot) {
        super(id, slot, 400000, 5000, Rarity.UNCOMMON);
        this.slot = slot;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide()) {
            if (ticker++ % tickRate == 0) {
                if (player.isOnFire() && hasFullSetNano(player)) {
                    if (hasEnergy(stack, this.energyForExtinguish)) {
                        for (int i = 0; i < player.getInventory().items.size(); i++) {
                            player.getInventory().getItem(i);
                            ItemStack mainItem = player.getInventory().getItem(i).copy();
                            if (mainItem.getItem() == Items.ICE) {
                                if (player.getInventory().getItem(i).getCount() >= 1) {
                                    player.getInventory().getItem(i).shrink(1);
                                }
                                useEnergy(stack, this.energyForExtinguish, false);
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
        if (slot == this.slot) {
            if (hasEnergy(stack, this.energyPerDamage)) {
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

    public boolean hasFullSetNano(Player player) {
        return player.getInventory().armor.get(0).getItem() instanceof ItemNanoArmor &&
                player.getInventory().armor.get(1).getItem() instanceof ItemNanoArmor &&
                player.getInventory().armor.get(2).getItem() instanceof ItemNanoArmor &&
                player.getInventory().armor.get(3).getItem() instanceof ItemNanoArmor;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onFallEvent(LivingFallEvent e) {
        LivingEntity entity = e.getEntity();
        Level level = entity.level;

        if (!level.isClientSide()) {
            ItemStack feetArmor = entity.getItemBySlot(EquipmentSlot.FEET);
            if (feetArmor.getItem() instanceof ItemNanoArmor armor) {
                float fallDamage = e.getDistance(); // 100% of fall damage
                if (fallDamage > 5) {
                    fallDamage *= 0.25F; // 100% of fall damage from above 5
                    int energy = (int) Math.min(fallDamage * armor.energyPerDamage, armor.getStoredEnergy(feetArmor));
                    armor.useEnergy(feetArmor, energy, false);
                    entity.hurt(DamageSource.FALL, fallDamage);
                    e.setCanceled(true);
                }
            }
        }
    }
}
