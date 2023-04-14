package com.mods.omnigears.items;

import cofh.core.item.EnergyContainerItem;
import com.mods.omnigears.Helpers;
import com.mods.omnigears.OmniGears;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static cofh.lib.util.helpers.StringHelper.getScaledNumber;
import static cofh.lib.util.helpers.StringHelper.localize;

public class ItemBaseElectricItem extends EnergyContainerItem {

    int maxCapacity, transfer;

    public ItemBaseElectricItem(int maxCapacity, int transfer) {
        super(new Properties().setNoRepair().tab(OmniGears.TAB).stacksTo(1), maxCapacity, transfer, transfer);
        this.maxCapacity = maxCapacity;
        this.transfer = transfer;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Helpers.formatColor(localize("info.cofh.energy") + ": " + getScaledNumber(getEnergyStored(stack)) + " / " + getScaledNumber(getMaxEnergyStored(stack)) + " RF", ChatFormatting.GRAY));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowedIn(group))
            Helpers.addChargeVariants(this, items);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        //Ignore NBT for energized items causing re-equip animations
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        //Ignore NBT for energized items causing block break reset
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00;
    }

    @Override
    public Capability<? extends IEnergyStorage> getEnergyCapability() {
        return ForgeCapabilities.ENERGY;
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
        return this.maxCapacity;
    }
}
