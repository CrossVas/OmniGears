package com.mods.omnigears.items.armors.base;

import cofh.core.item.ArmorItemCoFH;
import cofh.lib.api.item.IEnergyContainerItem;
import cofh.lib.energy.EnergyContainerItemWrapper;
import cofh.lib.util.helpers.StringHelper;
import com.mods.omnigears.OmniGears;
import com.mods.omnigears.utils.Helpers;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBaseElectricArmor extends ArmorItemCoFH implements IEnergyContainerItem, IEnergyPack {

    public int capacity, transfer;

    public ItemBaseElectricArmor(String id, EquipmentSlot slot, int capacity, int transfer, Rarity rarity) {
        super(new ArmorMaterialOmni(id), slot, new Properties().stacksTo(1).setNoRepair().tab(OmniGears.TAB).rarity(rarity));
        this.capacity = capacity;
        this.transfer = transfer;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide()) {
            if (player.getInventory().armor.get(2) == stack) {
                int extract = this.getExtract(stack);
                for (ItemStack item : player.getInventory().items) {
                    if (!(item.getItem() instanceof IEnergyPack) && canProvideEnergy(stack)) {
                        item.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(e ->
                                this.extractEnergy(stack, e.receiveEnergy(Math.min(extract, this.getEnergyStored(stack)), false), player.getAbilities().instabuild));
                    }
                }
            }
        }
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

}
