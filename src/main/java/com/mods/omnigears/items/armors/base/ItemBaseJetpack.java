package com.mods.omnigears.items.armors.base;

import cofh.lib.api.item.IEnergyContainerItem;
import com.mods.omnigears.client.keyboard.KeyboardHandler;
import com.mods.omnigears.Helpers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBaseJetpack extends ItemBaseElectricArmor implements IEnergyPack, IBoostProvider {

    public int usage;
    public static boolean lastUsed;
    public static final String TAG_ENABLED = "enabled";
    public static final String TAG_TOGGLE_TIMER = "toggleTimer";
    public static final String TAG_JET_TICKER = "jetpackTicker";
    public static final String TAG_HOVER = "hover";
    public static final String TAG_FUEL_TIME = "fuelTime";

    public ItemBaseJetpack(String id, int capacity, int transfer, Rarity rarity) {
        super(id, EquipmentSlot.CHEST, capacity, transfer, rarity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        boolean isEngineOn = getEngineStatus(stack);
        boolean isHoverOn = getHoverMode(stack);

        ChatFormatting engineColor = isEngineOn ? ChatFormatting.GREEN : ChatFormatting.RED;
        ChatFormatting hoverColor = isHoverOn ? ChatFormatting.GREEN : ChatFormatting.RED;

        String engineStatus = isEngineOn ? "message.text.on" : "message.text.off";
        String hoverStatus = isHoverOn ? "message.text.on" : "message.text.off";

        tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.jetpack.engine", engineColor, engineStatus));
        tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.jetpack.hover", hoverColor, hoverStatus));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);
        Entity entity = player.getRootVehicle();
        CompoundTag tag = Helpers.getCompoundTag(stack);
        boolean enabled = tag.getBoolean(TAG_ENABLED);
        byte jetpackTicker = tag.getByte(TAG_JET_TICKER);
        byte toggleTimer = tag.getByte(TAG_TOGGLE_TIMER);
        boolean server = !level.isClientSide();
        if (!enabled) {
            if (jetpackTicker > 0) {
                --jetpackTicker;
                tag.putByte(TAG_JET_TICKER, jetpackTicker);
            } else if (KeyboardHandler.instance.isToggleKeyDown(player)) {
                tag.putByte(TAG_JET_TICKER, (byte) 5);
                tag.putBoolean(TAG_ENABLED, true);
                if (server) {
                    player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.jetpack.engine", ChatFormatting.GREEN, "message.text.on"), false);
                }
            }
        } else if (KeyboardHandler.instance.isToggleKeyDown(player) && jetpackTicker <= 0) {
            tag.putByte(TAG_JET_TICKER, (byte) 5);
            tag.putBoolean(TAG_ENABLED, false);
            if (server) {
                player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.jetpack.engine", ChatFormatting.RED, "message.text.off"), false);
            }

        } else {
            if (jetpackTicker > 0) {
                --jetpackTicker;
                tag.putByte(TAG_JET_TICKER, jetpackTicker);
            }

            if (server) {
                int accellerationTicker = player.getPersistentData().getInt("SpecialMovementTicker");
                if (accellerationTicker > 0) {
                    CompoundTag persistentData = player.getPersistentData();
                    --accellerationTicker;
                    persistentData.putInt("SpecialMovementTicker", accellerationTicker);
                    if (accellerationTicker == 0) {
                        player.getPersistentData().putBoolean("SpecialMovement", false);
                    }
                }
            }

            boolean isHover = tag.getBoolean(TAG_HOVER);

            if (Minecraft.getInstance().options.keyJump.isDown() && KeyboardHandler.instance.isModeSwitchKeyDown(player) && !KeyboardHandler.instance.isAltKeyDown(player) && toggleTimer <= 0) {
                toggleTimer = 10;
                changeHoverMode(stack, player);
            }

            if (Minecraft.getInstance().options.keyJump.isDown() || isHover && !entity.isOnGround() && entity.getDeltaMovement().y < -0.15) {
                lastUsed = useJetpack(stack, player, entity, isHover);
            }

            if (toggleTimer > 0) {
                --toggleTimer;
                tag.putByte(TAG_TOGGLE_TIMER, toggleTimer);
            }

            byte time = tag.getByte(TAG_FUEL_TIME);
            if (time > 0) {
                --time;
                tag.putByte(TAG_FUEL_TIME, time);
            }
        }
    }

    public void changeHoverMode(ItemStack stack, Player player) {
        CompoundTag tag = Helpers.getCompoundTag(stack);
        Component message;
        if (tag.getBoolean(TAG_HOVER)) {
            message = Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.jetpack.hover", ChatFormatting.RED, "message.text.off");
            tag.putBoolean(TAG_HOVER, false);
        } else {
            message = Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.jetpack.hover", ChatFormatting.GREEN, "message.text.on");
            tag.putBoolean(TAG_HOVER, true);
        }
        if (!player.level.isClientSide()) {
            player.displayClientMessage(message, false);
        }
    }

    public boolean useJetpack(ItemStack stack, Player player, Entity affectedEntity, boolean hover) {
        int usageMultiplier = 1;
        int energy = ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack);
        if (energy <= 0) {
            return false;
        } else {
            float power = 0.7F;
            float dropPercentage = 0.05F;
            if ((double) energy / (double) this.capacity <= (double) dropPercentage) {
                power = (float) ((double) power * ((double) energy / (double) ((float) this.capacity * dropPercentage)));
            }

            if (Minecraft.getInstance().options.keyUp.isDown()) {
                float thruster = hover ? 0.65F : 0.3F;
                float forwardPower = power * thruster * 2.0F;
                float friction = 0F;

                if (forwardPower > 0.0F) {
                    if (canProvideBoost(stack) && KeyboardHandler.instance.isBoostKeyDown(player)) {
                        usageMultiplier = 3;
                        if (hover) {
                            friction = 0.18F;
                        } else friction = 0.14F;
                    }
                    affectedEntity.moveRelative(0.02F + friction, new Vec3(0.0, 0.0, 0.4 * (double) forwardPower));
                }
            }
            int maxFlightHeight = 350;
            double y = affectedEntity.getY();
            if (y > (double) (maxFlightHeight - 25)) {
                if (y > (double) maxFlightHeight) {
                    y = maxFlightHeight;
                }

                power = (float) ((double) power * (((double) maxFlightHeight - y) / 25.0));
            }

            Vec3 motion = affectedEntity.getDeltaMovement();
            CompoundTag nbt = Helpers.getCompoundTag(stack);
            if (nbt.getBoolean("SpecialMode") && !player.isOnGround()) {
                affectedEntity.setDeltaMovement(motion.add(0.0, Math.min(motion.y + (double) (power * 0.2F), 0.6000000238418579), 0.0));
                if (!player.level.isClientSide()) {
                    player.getPersistentData().putBoolean("SpecialMovement", affectedEntity.getDeltaMovement().y < 0.0);
                    player.getPersistentData().putInt("SpecialMovementTicker", 40);
                }
            } else {
                affectedEntity.setDeltaMovement(motion.x, Math.min(motion.y + (double) (power * 0.2F), 0.6000000238418579), motion.z);
            }

            if (hover) {
                float maxHoverY = 0F;
                int boostMultiplier = 1;
                if (canProvideBoost(stack) && KeyboardHandler.instance.isBoostKeyDown(player)) {
                    boostMultiplier = 4;
                }
                if (!player.isShiftKeyDown() || !Minecraft.getInstance().options.keyJump.isDown()) {
                    maxHoverY = -0.1F * boostMultiplier;
                    if (Minecraft.getInstance().options.keyJump.isDown()) {
                        maxHoverY = 0.2F * boostMultiplier;
                    }
                }

                Vec3 affectedMotion = affectedEntity.getDeltaMovement();
                if (affectedMotion.y > (double) maxHoverY) {
                    affectedEntity.setDeltaMovement(affectedMotion.x, Math.max(motion.y, (double) maxHoverY), affectedMotion.z);
                }
            }

            byte time = nbt.getByte(TAG_FUEL_TIME);
            if (time == 0) {
                this.extractEnergy(stack, usage * usageMultiplier, false);
            }
            if (!nbt.getBoolean("SpecialMode")) {
                affectedEntity.fallDistance = 0.0F;
                affectedEntity.walkDist = 0.0F;
                resetPlayerInAirTime(player);
            }

            return true;
        }
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canProvideBoost(ItemStack stack) {
        return false;
    }

    public void resetPlayerInAirTime(Entity player) {
        if (player instanceof ServerPlayer) {
            player.getRootVehicle().getSelfAndPassengers().forEach(Entity::resetFallDistance);
        }
    }

    public static boolean getEngineStatus(ItemStack stack) {
        CompoundTag tag = Helpers.getCompoundTag(stack);
        return tag.getBoolean(TAG_ENABLED);
    }

    public static boolean getHoverMode(ItemStack stack) {
        CompoundTag tag = Helpers.getCompoundTag(stack);
        return tag.getBoolean(TAG_HOVER);
    }
}
