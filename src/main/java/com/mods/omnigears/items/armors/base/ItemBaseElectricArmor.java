package com.mods.omnigears.items.armors.base;

import cofh.lib.api.item.IEnergyContainerItem;
import cofh.lib.energy.EnergyContainerItemWrapper;
import cofh.lib.util.helpers.StringHelper;
import com.mods.omnigears.Helpers;
import com.mods.omnigears.OmniGears;
import com.mods.omnigears.client.keyboard.KeyboardHandler;
import com.mods.omnigears.items.armors.intefaces.IEnergyProvider;
import com.mods.omnigears.items.armors.intefaces.IOverlayProvider;
import com.mods.omnigears.items.armors.intefaces.IProtectionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBaseElectricArmor extends ArmorItem implements IEnergyContainerItem, IEnergyProvider, IProtectionProvider, IOverlayProvider {

    public int capacity, transfer;
    public byte ticker, tickRate = 10;
    public int energyPerDamage = 250;

    public static final String TAG_CHARGER = "charger";
    public static final String TAG_TOGGLE_TICKER = "toggleTicker";

    public ItemBaseElectricArmor(String id, EquipmentSlot slot, int capacity, int transfer, Rarity rarity) {
        super(new ArmorMaterialOmni(id), slot, new Item.Properties().stacksTo(1).setNoRepair().tab(OmniGears.TAB).rarity(rarity));
        this.capacity = capacity;
        this.transfer = transfer;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (allowedIn(tab))
            Helpers.addChargeVariants(this, items);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {

        CompoundTag tag = Helpers.getCompoundTag(stack);
        boolean charger = tag.getBoolean(TAG_CHARGER);
        byte ticker = tag.getByte(TAG_TOGGLE_TICKER);
        Component message;

        if (stack.getItem() instanceof IEnergyProvider energyProvider && KeyboardHandler.isChargerKeyDown() && ticker <= 0) {
            if (energyProvider.canProvideEnergy(stack)) {
                ticker = 10;
                tag.putByte(TAG_TOGGLE_TICKER, ticker);
                if (!charger) {
                    tag.putBoolean(TAG_CHARGER, true);
                    message = Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.charger", ChatFormatting.GREEN, "message.text.on");
                } else {
                    tag.putBoolean(TAG_CHARGER, false);
                    message = Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.charger", ChatFormatting.RED, "message.text.off");
                }
                if (!player.level.isClientSide()) {
                    player.displayClientMessage(message, false);
                }
            }
        }

        if (ticker > 0) {
            --ticker;
            tag.putByte(TAG_TOGGLE_TICKER, ticker);
        }

        if (!level.isClientSide() && charger) {
            if (player.getItemBySlot(EquipmentSlot.CHEST) == stack) {
                int extract = this.getExtract(stack);
                for (ItemStack item : player.getInventory().items) {
                    if (!(item.getItem() instanceof IEnergyProvider) && canProvideEnergy(stack)) {
                        item.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(e ->
                                this.extractEnergy(stack, e.receiveEnergy(Math.min(extract, this.getEnergyStored(stack)), false), player.getAbilities().instabuild));
                    }
                }
            }
        }
    }

    public static boolean isChargingMode(ItemStack stack) {
        CompoundTag tag = Helpers.getCompoundTag(stack);
        return tag.getBoolean(TAG_CHARGER);
    }

    public boolean hasEnergy(ItemStack stack, int amount) {
        return getEnergyStored(stack) >= amount;
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getEnergyStored(stack) > 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.getTag() == null) {
            return 0;
        }
        return (int) Math.round(13.0D * getEnergyStored(stack) / (double) getMaxEnergyStored(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Helpers.formatColor(StringHelper.localize("info.cofh.energy") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF", ChatFormatting.GRAY));
    }

    @Override
    public Capability<? extends IEnergyStorage> getEnergyCapability() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new EnergyContainerItemWrapper(stack, this, getEnergyCapability());
    }

    @Override
    public int getExtract(ItemStack container) {
        return this.transfer;
    }

    @Override
    public int getReceive(ItemStack container) {
        return this.transfer;
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return this.capacity;
    }

    @Override
    public boolean isFullSet(Player player) {
        return false;
    }

    @Override
    public boolean provideProtection() {
        return false;
    }

    @Override
    public void useEnergy(ItemStack stack, int amount) {
        extractEnergy(stack, amount, false);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHurtEvent(LivingHurtEvent e) {
        if (e.getEntity() instanceof Player player) {
            float damage = e.getAmount();
            if (e.isCanceled()) return;
            if (damage <= 0) return;
            float realDamage = Math.max(0.5F, damage * 1.5F);
            player.getArmorSlots().forEach(stack -> {
                if (stack.getItem() instanceof ItemBaseElectricArmor armor) {
                    if (armor.provideProtection() && hasEnergy(stack, armor.energyPerDamage)) {
                        int energy = Math.min((int) (realDamage * armor.energyPerDamage), getEnergyStored(stack));
                        armor.useEnergy(stack, energy);
                        if (armor.isFullSet(player)) {
                            e.setCanceled(true);
                        } else {
                            player.hurt(DamageSource.FALL, damage * 0.05f);
                        }
                    }
                }
            });
        }
    }
}
