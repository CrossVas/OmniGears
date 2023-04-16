package com.mods.omnigears.client;

import cofh.lib.api.item.IEnergyContainerItem;
import com.mods.omnigears.Helpers;
import com.mods.omnigears.OmniConfig;
import com.mods.omnigears.items.armors.ItemAdvancedOmniArmor;
import com.mods.omnigears.items.armors.ItemBaseEnergyPack;
import com.mods.omnigears.items.armors.base.ItemBaseElectricArmor;
import com.mods.omnigears.items.armors.base.ItemBaseJetpack;
import com.mods.omnigears.items.armors.intefaces.IEnergyProvider;
import com.mods.omnigears.items.armors.intefaces.IOverlayProvider;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class OmniOverlay implements IGuiOverlay {
    public Minecraft mc;
    public Font fontRenderer;

    public static int offset = 3;
    public static int yPos = offset;
    public static int yPos1, yPos2, yPos3;

    public OmniOverlay(Minecraft mc) {
        this.mc = mc;
        this.fontRenderer = mc.font;
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Player player = this.mc.player;
        ItemStack armorStack = player.getItemBySlot(EquipmentSlot.CHEST);
        Item armorItem = armorStack.getItem();
        if (armorItem instanceof IOverlayProvider && armorItem instanceof IEnergyContainerItem energyItem) {
            int curCharge = energyItem.getEnergyStored(armorStack);
            int maxCharge = energyItem.getMaxEnergyStored(armorStack);
            int chargeLevel = (int) Math.round((double) curCharge / (double) maxCharge * 100);

            /** ENERGY STATUS GENERAL */

            Component charger = Helpers.formatColor("", ChatFormatting.GRAY);
            if (armorItem instanceof IEnergyProvider energyProvider) {
                boolean isCharging = ItemBaseElectricArmor.isChargingMode(armorStack);
                String chargerStatus = "message.text.charger.short";
                ChatFormatting chargerColor = isCharging ? ChatFormatting.GREEN : ChatFormatting.RED;
                if (energyProvider.canProvideEnergy(armorStack)) {
                    charger = Helpers.formatColor(" - ", ChatFormatting.GRAY).append(Helpers.formatSimpleMessage(chargerColor, chargerStatus));
                }
            }
            String energyStatus = "message.text.energy";
            Component energyToDisplay = Helpers.formatComplexMessage(ChatFormatting.WHITE, energyStatus, getEnergyTextColor(chargeLevel), chargeLevel + "%").append(charger);

            // Hover Start

            boolean isHoverOn = ItemBaseJetpack.getHoverMode(armorStack);
            String hoverStatus = isHoverOn ? "message.text.on" : "message.text.off";
            ChatFormatting hoverStatusColor = isHoverOn ? ChatFormatting.GREEN : ChatFormatting.RED;

            /** HOVER STATUS GENERAL */

            String hoverString = "message.text.jetpack.hover";
            Component hoverToDisplay = Helpers.formatComplexMessage(ChatFormatting.AQUA, hoverString, hoverStatusColor, hoverStatus);

            // Jetpack Engine Starts

            boolean isEngineOn = ItemBaseJetpack.getEngineStatus(armorStack);
            String engineStatus = isEngineOn ? "message.text.on" : "message.text.off";
            ChatFormatting engineStatusColor = isEngineOn ? ChatFormatting.GREEN : ChatFormatting.RED;

            /** ENGINE STATUS GENERAL */

            String engineString = "message.text.jetpack.engine";
            Component engineToDisplay = Helpers.formatComplexMessage(ChatFormatting.AQUA, engineString, engineStatusColor, engineStatus);

            // Gravi Engine Starts

            boolean isGraviEngineOn = ItemAdvancedOmniArmor.getEngineStatus(armorStack);
            String graviEngineStatus = isGraviEngineOn ? "message.text.on" : "message.text.off";
            ChatFormatting graviEngineStatusColor = isGraviEngineOn ? ChatFormatting.GREEN : ChatFormatting.RED;

            /** GRAVI ENGINE STATUS GENERAL */

            String graviEngineString = "message.text.gravitation";
            Component graviEngineToDisplay = Helpers.formatComplexMessage(ChatFormatting.AQUA, graviEngineString, graviEngineStatusColor, graviEngineStatus);

            // Levitation starts

            boolean isLevitationOn = ItemAdvancedOmniArmor.getWorkStatus(armorStack);
            String levitationStatus = isLevitationOn ? "message.text.on" : "message.text.off";
            ChatFormatting levitationStatusColor = isLevitationOn ? ChatFormatting.GREEN : ChatFormatting.RED;

            /** Levitation STATUS GENERAL */

            String levitationString = "message.text.levitation";
            Component levitationToDisplay = Helpers.formatComplexMessage(ChatFormatting.AQUA, levitationString, levitationStatusColor, levitationStatus);

            switch (OmniConfig.HUD_POS.get()) {
                case 1, 2 -> {
                    yPos1 = yPos = offset;
                    yPos2 = yPos1 + offset + mc.font.lineHeight;
                    yPos3 = yPos2 + offset + mc.font.lineHeight;
                }
                case 3, 4 -> {
                    yPos = screenHeight - (fontRenderer.lineHeight + offset);
                    yPos1 = screenHeight - ((fontRenderer.lineHeight * 3) + offset * 3);
                    yPos2 = yPos1 + offset + mc.font.lineHeight;
                    yPos3 = yPos2 + offset + mc.font.lineHeight;
                }
            }

            if (OmniConfig.SHOW_HUD.get()) {
                if (armorItem instanceof ItemBaseEnergyPack) {
                    fontRenderer.drawShadow(poseStack, energyToDisplay, getXOffset(energyToDisplay.getString(), gui.getMinecraft().getWindow()), yPos1, 0);
                }
                if (armorItem instanceof ItemAdvancedOmniArmor) {
                    fontRenderer.drawShadow(poseStack, energyToDisplay, getXOffset(energyToDisplay.getString(), gui.getMinecraft().getWindow()), yPos1, 0);
                    fontRenderer.drawShadow(poseStack, graviEngineToDisplay, getXOffset(graviEngineToDisplay.getString(), gui.getMinecraft().getWindow()), yPos2, 0);
                    fontRenderer.drawShadow(poseStack, levitationToDisplay, getXOffset(levitationToDisplay.getString(), gui.getMinecraft().getWindow()), yPos3, 0);
                }
                if (armorItem instanceof ItemBaseJetpack) {
                    fontRenderer.drawShadow(poseStack, energyToDisplay, getXOffset(energyToDisplay.getString(), gui.getMinecraft().getWindow()), yPos1, 0);
                    fontRenderer.drawShadow(poseStack, engineToDisplay, getXOffset(engineToDisplay.getString(), gui.getMinecraft().getWindow()), yPos2, 0);
                    fontRenderer.drawShadow(poseStack, hoverToDisplay, getXOffset(hoverToDisplay.getString(), gui.getMinecraft().getWindow()), yPos3, 0);
                }
            }
        }
    }

    private int getXOffset(String value, Window window) {
        int xPos = 0;
        switch (OmniConfig.HUD_POS.get()) {
            case 1, 3 -> xPos = offset;
            case 2, 4 -> xPos = window.getGuiScaledWidth() - this.mc.font.width(value) - offset;
            default -> {
            }
        }
        return xPos;
    }

    public ChatFormatting getEnergyTextColor(double energyLevel) {
        if (energyLevel >= 90) {
            return ChatFormatting.GREEN;
        }
        if ((energyLevel <= 90) && (energyLevel > 75)) {
            return ChatFormatting.YELLOW;
        }
        if ((energyLevel <= 75) && (energyLevel > 50)) {
            return ChatFormatting.GOLD;
        }
        if ((energyLevel <= 50) && (energyLevel > 35)) {
            return ChatFormatting.RED;
        }
        if (energyLevel <= 35) {
            return ChatFormatting.DARK_RED;
        }
        return ChatFormatting.WHITE;
    }
}

