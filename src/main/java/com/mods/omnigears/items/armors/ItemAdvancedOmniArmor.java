package com.mods.omnigears.items.armors;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mods.omnigears.client.keyboard.KeyboardHandler;
import com.mods.omnigears.items.armors.base.IProtectionProvider;
import com.mods.omnigears.items.armors.base.ItemBaseElectricArmor;
import com.mods.omnigears.Helpers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemAdvancedOmniArmor extends ItemBaseElectricArmor implements IProtectionProvider {

    public boolean gravitation;
    public boolean levitation;
    public static final String TAG_GRAVITATION = "enabled";
    public static final String TAG_LEVITATION = "levitation";
    public static final String TICKER = "ticker";
    public int energyForExtinguish = 100000;

    public ItemAdvancedOmniArmor() {
        super("advanced_omniarmor", EquipmentSlot.CHEST, 40000000, 200000, Rarity.RARE);
        this.energyPerDamage = 2000;
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFullSet(Player player) {
        return true;
    }

    @Override
    public boolean provideProtection() {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        boolean isEngineOn = getEngineStatus(stack);
        boolean isLevitationOn = getWorkStatus(stack);
        String engineStatus = isEngineOn ? "message.text.on" : "message.text.off";
        ChatFormatting engineColor = isEngineOn ? ChatFormatting.GREEN : ChatFormatting.RED;
        String levitationStatus = isLevitationOn ? "message.text.on" : "message.text.off";
        ChatFormatting levitationColor = levitation ? ChatFormatting.GREEN : ChatFormatting.RED;
        tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.gravitation", engineColor, engineStatus));
        tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.levitation", levitationColor, levitationStatus));
        super.appendHoverText(stack, level, tooltip, flag);
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
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);
        CompoundTag tag = Helpers.getCompoundTag(stack);
        boolean enabled = tag.getBoolean(TAG_GRAVITATION);
        byte engineTicker = tag.getByte(TICKER);
        boolean server = !level.isClientSide();

        if (engineTicker > 0) {
            --engineTicker;
            tag.putByte(TICKER, engineTicker);
        }

        if (KeyboardHandler.instance.isToggleKeyDown(player) && !Minecraft.getInstance().isPaused() && engineTicker <= 0) {
            tag.putByte(TICKER, (byte) 5);
            if (!enabled) {
                tag.putBoolean(TAG_GRAVITATION, true);
                player.getAbilities().mayfly = true;
                gravitation = true;
                if (levitation)
                    player.getAbilities().flying = true;
                if (server)
                    player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.AQUA, "message.text.gravitation", ChatFormatting.GREEN, "message.text.on"), false);
            } else {
                tag.putBoolean(TAG_GRAVITATION, false);
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                gravitation = false;
                if (server) {
                    player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.AQUA, "message.text.gravitation", ChatFormatting.RED, "message.text.off"), false);
                }
            }
        } else {
            if (server) {
                int accellerationTicker = player.getPersistentData().getInt("SpecialMovementTicker");
                if (accellerationTicker > 0) {
                    CompoundTag playerData = player.getPersistentData();
                    --accellerationTicker;
                    playerData.putInt("SpecialMovementTicker", accellerationTicker);
                    if (accellerationTicker == 0) {
                        player.getPersistentData().putBoolean("SpecialMovement", false);
                    }
                }
            }

            if (KeyboardHandler.isJumpKeyDown() && KeyboardHandler.instance.isModeSwitchKeyDown(player) && engineTicker <= 0) {
                tag.putByte(TICKER, (byte) 5);
                if (tag.getBoolean(TAG_LEVITATION)) {
                    saveWorkMode(stack, false);
                    levitation = false;
                    player.getAbilities().flying = false;

                    if (server) {
                        player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.AQUA, "message.text.levitation", ChatFormatting.RED, "message.text.off"), false);
                    }
                } else {
                    saveWorkMode(stack, true);
                    levitation = true;
                    if (gravitation) {
                        player.getAbilities().flying = true;
                    }
                    if (server) {
                        player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.AQUA, "message.text.levitation", ChatFormatting.GREEN, "message.text.on"), false);
                    }
                }
            }

            if (gravitation || (player.getAbilities().flying && !player.isOnGround())) {
                this.extractEnergy(stack, 1500, false);
            }
        }

        if (server) {
            if (ticker++ % tickRate == 0) {
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

    // Returns gravity engine status
    public static boolean getEngineStatus(ItemStack stack) {
        CompoundTag tag = Helpers.getCompoundTag(stack);
        return tag.getBoolean(TAG_GRAVITATION);
    }

    // Returns levitation status
    public static boolean getWorkStatus(ItemStack stack) {
        CompoundTag tag = Helpers.getCompoundTag(stack);
        return tag.getBoolean(TAG_LEVITATION);
    }

    public void saveWorkMode(ItemStack stack, boolean is) {
        CompoundTag tag = Helpers.getCompoundTag(stack);
        tag.putBoolean(TAG_LEVITATION, is);
    }
}
