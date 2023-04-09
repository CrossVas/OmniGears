package com.mods.omnigears;

import cofh.lib.api.item.IEnergyContainerItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Helpers {

    public static ItemStack getCharged(Item item, int charge) {
        if (item instanceof IEnergyContainerItem electricItem) {
            ItemStack chargedItem = new ItemStack(item);
            electricItem.setEnergyStored(chargedItem, charge);
            return chargedItem;
        } else {
            return null;
        }
    }

    public static void addChargeVariants(Item item, List<ItemStack> list) {
        list.add(getCharged(item, 0));
        list.add(getCharged(item, Integer.MAX_VALUE));
    }

    public static MutableComponent formatColor(String text, ChatFormatting style) {
        return Component.literal(text).withStyle(style);
    }

    public static MutableComponent formatSimpleMessage(ChatFormatting color, String text) {
        return Component.translatable(text).withStyle(color);
    }

    public static MutableComponent formatComplexMessage(ChatFormatting color1, String text1, ChatFormatting color2, String text2) {
        return formatSimpleMessage(color1, text1).append(formatSimpleMessage(color2, text2));
    }

    public static List<BlockPos> findPositions(BlockState state, BlockPos location, Level world, int maxRange) {
        List<BlockPos> found = new ArrayList<>();
        Set<BlockPos> checked = new ObjectOpenHashSet<>();
        found.add(location);
        Block startBlock = state.getBlock();
        int maxCount = maxRange - 1;
        for (int i = 0; i < found.size(); i++) {
            BlockPos blockPos = found.get(i);
            checked.add(blockPos);
            for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
                // We can check contains as mutable
                if (!checked.contains(pos)) {
                    if (!world.getBlockState(pos).isAir()) {
                        if (startBlock == world.getBlockState(pos).getBlock()) {
                            // Make sure to add it as immutable
                            found.add(pos.immutable());
                        }
                        if (found.size() > maxCount) {
                            return found;
                        }
                    }

                }
            }
        }
        return found;
    }

    public static void veinArea(Level world, Player player, ItemStack stack, BlockPos origin, Collection<BlockPos> area, int energyCost) {
        boolean noEnergy = false;
        int energyLevel = ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack);

        for (BlockPos check : area) {
            if (energyLevel < energyCost) {
                noEnergy = true;
                break;
            }
            if (origin.equals(check)) {
                continue;
            }

            BlockState state = world.getBlockState(check);
            if (!state.isAir() && state.canHarvestBlock(world, check, player) && state.getDestroySpeed(world, origin) != -1.0F) {
                if (state.onDestroyedByPlayer(world, check, player, true, state.getFluidState())) {
                    Block block = state.getBlock();
                    BlockEntity tile = world.getBlockEntity(check);
                    block.destroy(world, check, state);
                    block.playerDestroy(world, player, check, state, tile, stack);
                    ((IEnergyContainerItem) stack.getItem()).extractEnergy(stack, energyCost, false);
                    if (player instanceof ServerPlayer serverPlayer) {
                        int exp = ForgeHooks.onBlockBreakEvent(world, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer, check);
                        if (exp == -1) {
                            continue;
                        }
                        if (exp > 0) {
                            block.popExperience((ServerLevel) world, check, exp);
                        }
                    }
                }
            }
        }

        if (noEnergy) {
            player.displayClientMessage(Helpers.formatSimpleMessage(ChatFormatting.RED, "message.text.no_energy"), false);
        }
    }

    public static CompoundTag getCompoundTag(ItemStack stack) {
        return !stack.hasTag() ? new CompoundTag() : stack.getTag();
    }

    public static class FakeUseOnContext extends UseOnContext {
        public FakeUseOnContext(UseOnContext original, ItemStack fakeItem) {
            super(original.getLevel(), original.getPlayer(), original.getHand(), fakeItem,
                    new BlockHitResult(original.getClickLocation(), original.getClickedFace(), original.getClickedPos(), original.isInside()));
        }
    }
}
