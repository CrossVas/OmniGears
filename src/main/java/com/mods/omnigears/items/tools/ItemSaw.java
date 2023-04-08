package com.mods.omnigears.items.tools;

import cofh.core.item.EnergyContainerItem;
import cofh.lib.util.Utils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mods.omnigears.OmniGears;
import com.mods.omnigears.client.Keyboard;
import com.mods.omnigears.utils.Helpers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.IForgeShearable;
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

import static cofh.lib.util.helpers.StringHelper.getScaledNumber;
import static cofh.lib.util.helpers.StringHelper.localize;

public class ItemSaw extends EnergyContainerItem {

    public static final Set<ToolAction> DEFAULT_ACTIONS = toolActions(ToolActions.AXE_DIG, ToolActions.AXE_STRIP, ToolActions.AXE_SCRAPE, ToolActions.AXE_WAX_OFF, ToolActions.SHEARS_DIG, ToolActions.SHEARS_HARVEST);
    public static final Set<Material> MATERIALS = new ObjectOpenHashSet<>();
    public static final Set<Enchantment> VALID_ENCHANTS = new ObjectOpenHashSet<>();

    public int ENERGY_PER_USE;
    public float EFFICIENCY;

    static {
        MATERIALS.add(Material.CAKE);
        MATERIALS.add(Material.EXPLOSIVE);
        MATERIALS.add(Material.FIRE);
        MATERIALS.add(Material.SPONGE);
        MATERIALS.add(Material.VEGETABLE);
        MATERIALS.add(Material.BAMBOO);
        MATERIALS.add(Material.BAMBOO_SAPLING);
        MATERIALS.add(Material.DECORATION);
        MATERIALS.add(Material.CLOTH_DECORATION);
        MATERIALS.add(Material.WOOL);
        MATERIALS.add(Material.WOOD);
        MATERIALS.add(Material.NETHER_WOOD);
        MATERIALS.add(Material.PLANT);
        MATERIALS.add(Material.WATER_PLANT);
        MATERIALS.add(Material.REPLACEABLE_FIREPROOF_PLANT);
        MATERIALS.add(Material.REPLACEABLE_PLANT);
        MATERIALS.add(Material.REPLACEABLE_WATER_PLANT);
        MATERIALS.add(Material.LEAVES);
        MATERIALS.add(Material.WEB);
        VALID_ENCHANTS.add(Enchantments.BLOCK_EFFICIENCY);
        VALID_ENCHANTS.add(Enchantments.SILK_TOUCH);
        VALID_ENCHANTS.add(Enchantments.MOB_LOOTING);
        VALID_ENCHANTS.add(Enchantments.SWEEPING_EDGE);
        VALID_ENCHANTS.add(Enchantments.SHARPNESS);
        VALID_ENCHANTS.add(Enchantments.SMITE);
        VALID_ENCHANTS.add(Enchantments.KNOCKBACK);
        VALID_ENCHANTS.add(Enchantments.FIRE_ASPECT);
    }

    public ItemSaw() {
        super(new Properties().setNoRepair().tab(OmniGears.TAB).stacksTo(1), 40000, 500, 500);
        this.ENERGY_PER_USE = 200;
        this.EFFICIENCY = 8.0F;
    }

    public ItemSaw(int maxEnergy, int extract, int receive) {
        super(new Properties().setNoRepair().tab(OmniGears.TAB).stacksTo(1), maxEnergy, extract, receive);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00;
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Helpers.formatColor(localize("info.cofh.energy") + ": " + getScaledNumber(getEnergyStored(stack)) + " / " + getScaledNumber(getMaxEnergyStored(stack)) + " RF", ChatFormatting.GRAY));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || VALID_ENCHANTS.contains(enchantment);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return (MATERIALS.contains(state.getMaterial()) || state.is(BlockTags.MINEABLE_WITH_AXE)) && hasEnergy(stack, ENERGY_PER_USE) ? getEfficiency(stack) : 1.0F;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return DEFAULT_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
            return TierSortingRegistry.isCorrectTierForDrops(Tiers.DIAMOND, state);
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
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (Utils.isServerWorld(worldIn) && state.getDestroySpeed(worldIn, pos) != 0.0F) {
            if (entityLiving instanceof Player player && !player.getAbilities().instabuild) {
                extractEnergy(stack, ENERGY_PER_USE, false);
            }
        }
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && !player.getAbilities().instabuild) {
            extractEnergy(stack, ENERGY_PER_USE * 2, false);
        }
        return true;
    }

    public float getEfficiency(ItemStack stack) {
        return hasEnergy(stack, ENERGY_PER_USE) ? EFFICIENCY : 1.0F;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!entity.level.isClientSide()) {
            if (entity instanceof IForgeShearable target) {
                BlockPos pos = entity.blockPosition();
                if (target.isShearable(stack, entity.level, pos) && hasEnergy(stack, ENERGY_PER_USE * 2)) {
                    RandomSource rand = entity.getRandom();
                    for (ItemStack item : target.onSheared(player, stack, entity.level, pos, EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack))) {
                        ItemEntity itemEntity = entity.spawnAtLocation(item, 1.0F);
                        itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                    }
                    extractEnergy(stack, ENERGY_PER_USE * 2, false);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (slot == EquipmentSlot.MAINHAND) {
            float damage = getAttackDamage(stack);
            if (damage != 0.0F) {
                multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", damage, AttributeModifier.Operation.ADDITION));
            }
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", 0.0F, AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    public float getAttackDamage(ItemStack stack) {
        return hasEnergy(stack, ENERGY_PER_USE) ? 10.0F : 1.0F;
    }

    @Override
    public Capability<? extends IEnergyStorage> getEnergyCapability() {
        return ForgeCapabilities.ENERGY;
    }

    public boolean hasEnergy(ItemStack stack, int amount) {
        return getEnergyStored(stack) >= amount;
    }

    public static class ItemAdvancedSaw extends ItemSaw {

        public static final String SHEAR_TAG = "shears";

        public ItemAdvancedSaw() {
            super(120000, 1000, 1000);
            this.ENERGY_PER_USE = 400;
            this.EFFICIENCY = 35.0F;
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
            super.appendHoverText(stack, world, tooltip, flagIn);
            boolean isShearsOn = getChainsawMode(stack);
            String status = isShearsOn ? "message.text.on" : "message.text.off";
            ChatFormatting color = isShearsOn ? ChatFormatting.GREEN : ChatFormatting.RED;
            tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.mode.shears", color, status));
        }

        public boolean getChainsawMode(ItemStack stack) {
            CompoundTag tag = Helpers.getCompoundTag(stack);
            return tag.getBoolean(SHEAR_TAG);
        }

        public void saveChainsawMode(ItemStack stack, boolean isShearOn) {
            CompoundTag tag = Helpers.getCompoundTag(stack);
            tag.putBoolean(SHEAR_TAG, isShearOn);
        }

        public void changeChainsawMode(ItemStack stack, Player player) {
            if (getChainsawMode(stack)) {
                saveChainsawMode(stack, false);
                player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.mode.shears", ChatFormatting.RED, "message.text.off"), false);
            } else {
                saveChainsawMode(stack, true);
                player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.mode.shears", ChatFormatting.GREEN, "message.text.on"), false);
                setActive(stack, true);
            }
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide()) {
                if (Keyboard.isModeSwitchKeyDown()) {
                    changeChainsawMode(stack, player);
                }
                return InteractionResultHolder.success(stack);
            } else {
                return InteractionResultHolder.fail(stack);
            }
        }

        @Override
        public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
            Level world = player.level;
            if (!world.isClientSide()) {
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                if (MATERIALS.contains(state.getMaterial())) {
                    if (getChainsawMode(stack)) {
                        if (hasEnergy(stack, ENERGY_PER_USE)) {
                            ItemStack drop = new ItemStack(block);
                            float f = 0.7F;
                            double d = (double) (player.level.random.nextFloat() * f) + (double) (1.0F - f) * 0.5;
                            double d1 = (double) (player.level.random.nextFloat() * f) + (double) (1.0F - f) * 0.5;
                            double d2 = (double) (player.level.random.nextFloat() * f) + (double) (1.0F - f) * 0.5;
                            ItemEntity itemEntity = new ItemEntity(player.level, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, drop);

                            itemEntity.setDefaultPickUpDelay();
                            player.level.addFreshEntity(itemEntity);
                            player.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                            player.awardStat(Stats.BLOCK_MINED.get(block));
                            player.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    }
                    extractEnergy(stack, ENERGY_PER_USE, false);
                    return super.onBlockStartBreak(stack, pos, player);
                }
            }
            return false;
        }
    }
}
