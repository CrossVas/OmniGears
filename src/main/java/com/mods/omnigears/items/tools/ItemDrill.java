package com.mods.omnigears.items.tools;

import cofh.core.item.EnergyContainerItem;
import cofh.lib.util.Utils;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.mods.omnigears.OmniGears;
import com.mods.omnigears.client.Keyboard;
import com.mods.omnigears.utils.Helpers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cofh.lib.util.helpers.StringHelper.getScaledNumber;
import static cofh.lib.util.helpers.StringHelper.localize;

public class ItemDrill extends EnergyContainerItem {

    public static final Set<ToolAction> DEFAULT_TOOL_ACTIONS = toolActions(ToolActions.PICKAXE_DIG, ToolActions.SHOVEL_DIG);
    public static final Set<Material> MATERIALS = new ObjectOpenHashSet<>();
    public static final Set<Enchantment> VALID_ENCHANTS = new ObjectOpenHashSet<>();
    public int ENERGY_PER_USE;
    public float EFFICIENCY;
    public Tier TIER;

    static {
        MATERIALS.add(Material.HEAVY_METAL);
        MATERIALS.add(Material.METAL);
        MATERIALS.add(Material.STONE);
        MATERIALS.add(Material.GLASS);
        MATERIALS.add(Material.BUILDABLE_GLASS);
        VALID_ENCHANTS.add(Enchantments.BLOCK_EFFICIENCY);
        VALID_ENCHANTS.add(Enchantments.SILK_TOUCH);
        VALID_ENCHANTS.add(Enchantments.BLOCK_FORTUNE);
    }

    public ItemDrill() {
        super(new Properties().setNoRepair().tab(OmniGears.TAB).stacksTo(1), 40000, 500, 500);
        this.TIER = Tiers.IRON;
        this.ENERGY_PER_USE = 200;
        this.EFFICIENCY = 8.0F;
    }

    public ItemDrill(Tier tier, int transfer, int capacity) {
        super(new Properties().setNoRepair().tab(OmniGears.TAB), capacity, transfer, transfer);
        this.extract = transfer;
        this.receive = transfer;
        this.maxEnergy = capacity;
        this.TIER = tier;
        this.ENERGY_PER_USE = 320;
        this.EFFICIENCY = 16.0F;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00;
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
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Helpers.formatColor(localize("info.cofh.energy") + ": " + getScaledNumber(getEnergyStored(stack)) + " / " + getScaledNumber(getMaxEnergyStored(stack)) + " RF", ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Player player = context.getPlayer();
        Direction face = context.getClickedFace();
        if (!world.isClientSide() && !player.isCrouching() && !Keyboard.isAltKeyDown() && !Keyboard.isModeSwitchKeyDown()) {
            int torchSlot = getTorchSlot(player.getInventory());
            if (torchSlot != -1) {
                if (face != Direction.DOWN) {
                    ItemStack fakeStack = new ItemStack(Blocks.TORCH);
                    InteractionResult result = fakeStack.useOn(new Helpers.FakeUseOnContext(context, fakeStack));
                    if (result.consumesAction()) {
                        SoundType soundType = Blocks.TORCH.defaultBlockState().getSoundType(world, pos, player);
                        world.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 0.8F);
                        int torchCnt = player.getInventory().getItem(torchSlot).getCount();
                        if (torchCnt > 0 && !player.isCreative()) {
                            player.getInventory().getItem(torchSlot).shrink(1);
                        }
                    }
                }
            }
        }
        return super.useOn(context);
    }

    public int getTorchSlot(Inventory inv) {
        int torchSlot = -1;
        for (int slot = 0; slot <= inv.getContainerSize(); slot++) {
            String itemName = inv.getItem(slot).getItem().getDescriptionId();
            if (itemName.contains("torch") && (!itemName.contains("redstone"))) {
                torchSlot = slot;
                break;
            }
        }
        return torchSlot;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || VALID_ENCHANTS.contains(enchantment);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return MATERIALS.contains(state.getMaterial()) || state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) ? getEfficiency(stack) : 0.5F;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return DEFAULT_TOOL_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return TierSortingRegistry.isCorrectTierForDrops(TIER, state);
        }
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            extractEnergy(stack, ENERGY_PER_USE, false);
        }
        return 0;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && !player.getAbilities().instabuild) {
            extractEnergy(stack, ENERGY_PER_USE * 2, false);
        }
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (Utils.isServerWorld(worldIn) && state.getDestroySpeed(worldIn, pos) != 0.0F) {
            if (entityLiving instanceof Player player && !player.getAbilities().instabuild) {
                extractEnergy(stack, ENERGY_PER_USE, false);
            }
        }
        return true;
    }

    @Override
    public Capability<? extends IEnergyStorage> getEnergyCapability() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public int getExtract(ItemStack container) {
        return this.extract;
    }

    @Override
    public int getReceive(ItemStack container) {
        return this.receive;
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return this.maxEnergy;
    }

    public float getEfficiency(ItemStack stack) {
        return hasEnergy(stack) ? EFFICIENCY : 0.5F;
    }

    public boolean hasEnergy(ItemStack stack) {
        return getEnergyStored(stack) >= ENERGY_PER_USE;
    }

    public static class ItemAdvancedDrill extends ItemDrill {

        public static String TAG_MODE = "toolMode";
        public static String TAG_PROPS = "toolProps";

        public ItemAdvancedDrill() {
            super(Tiers.NETHERITE, 5000, 120000);
            this.EFFICIENCY = 1.0F;
        }

        public static DrillMode getDrillMode(ItemStack stack) {
            CompoundTag tag = Helpers.getCompoundTag(stack);
            return DrillMode.getFromID(tag.getInt(TAG_MODE));
        }

        public static DrillMode getNextDrillMode(ItemStack stack) {
            CompoundTag tag = Helpers.getCompoundTag(stack);
            return DrillMode.getFromID(tag.getInt(TAG_MODE) + 1);
        }

        public static void saveDrillMode(ItemStack stack, DrillMode mode) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putInt(TAG_MODE, mode.ordinal());
        }

        public static DrillProps getDrillProps(ItemStack stack) {
            CompoundTag tag = Helpers.getCompoundTag(stack);
            return DrillProps.getFromId(tag.getInt(TAG_PROPS));
        }

        public static DrillProps getNextDrillProps(ItemStack stack) {
            CompoundTag tag = Helpers.getCompoundTag(stack);
            return DrillProps.getFromId(tag.getInt(TAG_PROPS) + 1);
        }

        public static void saveDrillProps(ItemStack stack, DrillProps props) {
            CompoundTag tag = Helpers.getCompoundTag(stack);
            tag.putInt(TAG_PROPS, props.ordinal());
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
            DrillMode mode = getDrillMode(stack);
            DrillProps props = getDrillProps(stack);
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
            tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.mode", mode.color, mode.name));
            tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.mode.eff", props.color, props.name));
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide()) {
                if (Keyboard.isModeSwitchKeyDown()) {
                    DrillMode mode = getNextDrillMode(stack);
                    saveDrillMode(stack, mode);
                    player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.mode", mode.color, mode.name), false);

                }
                if (Keyboard.isAltKeyDown()) {
                    DrillProps props = getNextDrillProps(stack);
                    saveDrillProps(stack, props);
                    player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.mode.eff", props.color, props.name), false);
                    this.EFFICIENCY = props.efficiency;
                    this.ENERGY_PER_USE = props.energyCost;
                }
                return InteractionResultHolder.success(stack);
            } else {
                return InteractionResultHolder.fail(stack);
            }
        }

        @Override
        public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
            Level world = player.level;
            DrillMode mode = getDrillMode(itemstack);
            DrillProps props = getDrillProps(itemstack);
            if (!world.isClientSide()) {
                if (mode == DrillMode.BIG_HOLES) {
                    List<BlockPos> area = getBreakableBlocksRadius(itemstack, pos, player, 1);
                    Helpers.veinArea(world, player, itemstack, pos, area, props.energyCost);
                }
            }
            return false;
        }

        public static ImmutableList<BlockPos> getBreakableBlocksRadius(ItemStack stack, BlockPos pos, Player player, int radius) {
            List<BlockPos> area;
            Level world = player.getCommandSenderWorld();
            BlockHitResult traceResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
            if (traceResult.getType() == HitResult.Type.MISS || player.isSecondaryUseActive() || radius <= 0) {
                return ImmutableList.of();
            }

            area = switch (traceResult.getDirection()) {
                case DOWN, UP ->
                        BlockPos.betweenClosedStream(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius))
                                .filter(blockPos -> canToolAffect(world, blockPos, pos))
                                .map(BlockPos::immutable)
                                .collect(Collectors.toList());
                case NORTH, SOUTH ->
                        BlockPos.betweenClosedStream(pos.offset(-radius, -radius, 0), pos.offset(radius, radius, 0))
                                .filter(blockPos -> canToolAffect(world, blockPos, pos))
                                .map(BlockPos::immutable)
                                .collect(Collectors.toList());
                default -> BlockPos.betweenClosedStream(pos.offset(0, -radius, -radius), pos.offset(0, radius, radius))
                        .filter(blockPos -> canToolAffect(world, blockPos, pos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
            };
            area.remove(pos);
            return ImmutableList.copyOf(area);
        }

        private static boolean canToolAffect(Level world, BlockPos offset, BlockPos origin) {
            BlockState originState = world.getBlockState(origin);
            BlockState offsetState = world.getBlockState(offset);
            float alphaStrength = originState.getDestroySpeed(world, origin);
            float strength = offsetState.getDestroySpeed(world, offset);
            return strength > 0.0F && alphaStrength / strength <= 10.0F;
        }

        public enum DrillMode {
            NORMAL(ChatFormatting.DARK_GREEN), BIG_HOLES(ChatFormatting.LIGHT_PURPLE);

            private static final DrillMode[] VALUES = values();
            public final ChatFormatting color;
            public final String name;

            DrillMode(ChatFormatting color) {
                this.name = "message.text.mode." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name());
                this.color = color;
            }

            public static DrillMode getFromID(int ID) {
                return VALUES[ID % VALUES.length];
            }
        }

        public enum DrillProps {
            NORMAL(35.0F, 640, ChatFormatting.LIGHT_PURPLE), LOW_POWER(16.0F, 320, ChatFormatting.GREEN),
            FINE(10.0F, 200, ChatFormatting.AQUA);

            private static final DrillProps[] VALUES = values();
            public final String name;
            public final ChatFormatting color;
            public final float efficiency;
            public final int energyCost;

            DrillProps(float efficiency, int energyCost, ChatFormatting color) {
                this.name = "message.text.mode." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name());
                this.color = color;
                this.efficiency = efficiency;
                this.energyCost = energyCost;
            }

            public static DrillProps getFromId(int id) {
                return VALUES[id % VALUES.length];
            }
        }
    }
}
