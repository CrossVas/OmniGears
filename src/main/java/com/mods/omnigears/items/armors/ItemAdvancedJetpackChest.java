package com.mods.omnigears.items.armors;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
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

import java.util.UUID;
import java.util.function.Consumer;

public class ItemAdvancedJetpackChest extends ItemElectricJetpack {

    public int energyForExtinguish = 50000;

    public ItemAdvancedJetpackChest() {
        super("advanced_jetpack_chest", 4000000, 5000, Rarity.UNCOMMON, 144, true, true);
        this.energyPerDamage = 1000;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);
        if (!level.isClientSide()) {
            if (level.getGameTime() % 2 == 0) {
                if (player.isOnFire()) {
                    if (hasEnergy(stack, this.energyForExtinguish)) {
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
        if (slot == EquipmentSlot.CHEST) {
            if (hasEnergy(stack, this.energyPerDamage)) {
                modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", 8, AttributeModifier.Operation.ADDITION));
                modifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor modifier", 2, AttributeModifier.Operation.ADDITION));
            }
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

    @Override
    public boolean isFullSet(Player player) {
        return true;
    }

    @Override
    public boolean provideProtection() {
        return true;
    }
}
