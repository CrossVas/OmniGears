package com.mods.omnigears.items.tools;

import cofh.lib.util.Utils;
import com.google.common.base.CaseFormat;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mods.omnigears.Helpers;
import com.mods.omnigears.client.keyboard.KeyboardHandler;
import com.mods.omnigears.items.ItemBaseElectricItem;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ItemOmni extends ItemBaseElectricItem {

    public static final Set<ToolAction> DEFAULT_TOOL_ACTIONS = toolActions(ToolActions.PICKAXE_DIG, ToolActions.SHOVEL_DIG, ToolActions.AXE_DIG);
    public static final Set<Enchantment> VALID_ENCHANTS = new ObjectOpenHashSet<>();
    public static final Set<Material> MATERIALS = new ObjectOpenHashSet<>();

    static {
        VALID_ENCHANTS.add(Enchantments.BLOCK_EFFICIENCY);
        VALID_ENCHANTS.add(Enchantments.SILK_TOUCH);
        VALID_ENCHANTS.add(Enchantments.BLOCK_FORTUNE);
        MATERIALS.add(Material.HEAVY_METAL);
        MATERIALS.add(Material.METAL);
        MATERIALS.add(Material.STONE);
        MATERIALS.add(Material.GLASS);
        MATERIALS.add(Material.BUILDABLE_GLASS);
        MATERIALS.add(Material.WOOL);
        MATERIALS.add(Material.LEAVES);
        MATERIALS.add(Material.EXPLOSIVE);
        MATERIALS.add(Material.FIRE);
        MATERIALS.add(Material.SPONGE);
        MATERIALS.add(Material.WEB);
        MATERIALS.add(Material.GRASS);
    }

    public static final String TAG_MODE = "toolMode";
    public static final String TAG_PROPS = "toolProps";
    public float EFFICIENCY;
    public int ENERGY_PER_USAGE;

    public ItemOmni() {
        super(1000000, 50000);
        this.EFFICIENCY = 1.0F;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        OmniMode mode = getOmniMode(stack);
        OmniProps props = getOmniProps(stack);
        tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.mode", mode.color, mode.name));
        tooltip.add(Helpers.formatComplexMessage(ChatFormatting.GOLD, "message.text.mode.eff", props.color, props.name));
        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    public static OmniMode getOmniMode(ItemStack drill) {
        CompoundTag tag = Helpers.getCompoundTag(drill);
        return OmniMode.getFromId(tag.getInt(TAG_MODE));
    }

    public static OmniMode getNextOmniMode(ItemStack drill) {
        CompoundTag tag = Helpers.getCompoundTag(drill);
        return OmniMode.getFromId(tag.getInt(TAG_MODE) + 1);
    }

    public static void saveOmniMode(ItemStack drill, OmniMode mode) {
        CompoundTag tag = Helpers.getCompoundTag(drill);
        tag.putInt(TAG_MODE, mode.ordinal());
    }

    public static OmniProps getOmniProps(ItemStack drill) {
        CompoundTag tag = Helpers.getCompoundTag(drill);
        return OmniProps.getFromId(tag.getInt(TAG_PROPS));
    }

    public static OmniProps getNextOmniProps(ItemStack drill) {
        CompoundTag tag = Helpers.getCompoundTag(drill);
        return OmniProps.getFromId(tag.getInt(TAG_PROPS) + 1);
    }

    public static void saveOmniProps(ItemStack drill, OmniProps props) {
        CompoundTag tag = Helpers.getCompoundTag(drill);
        tag.putInt(TAG_PROPS, props.ordinal());
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        OmniMode mode = getOmniMode(stack);
        OmniProps props = getOmniProps(stack);
        Level world = player.level;
        BlockState state = world.getBlockState(pos);
        boolean isOre = state.is(Tags.Blocks.ORES);
        boolean vein = (mode == OmniMode.VEIN && isOre) || (mode == OmniMode.VEIN_EXTENDED);

        if (!player.isCreative() && !player.isCrouching() && vein) {
            if (!world.isClientSide()) {
                List<BlockPos> ore_area = Helpers.findPositions(state, pos, world, 128);
                Helpers.veinArea(world, player, stack, pos, ore_area, props.energyCost);
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (KeyboardHandler.instance.isModeSwitchKeyDown(player)) {
                OmniMode mode = getNextOmniMode(stack);
                saveOmniMode(stack, mode);
                player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.mode", mode.color, mode.name), false);

            }
            if (KeyboardHandler.instance.isAltKeyDown(player)) {
                OmniProps props = getNextOmniProps(stack);
                saveOmniProps(stack, props);
                player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.YELLOW, "message.text.mode.eff", props.color, props.name), false);
                this.EFFICIENCY = props.efficiency;
                this.ENERGY_PER_USAGE = props.energyCost;
            }
            if (player.isCrouching()) {
                Map<Enchantment, Integer> enchMap = new IdentityHashMap<>();
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player) == 0) {
                    enchMap.put(Enchantments.SILK_TOUCH, 1);
                    player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.LIGHT_PURPLE, "message.text.mode.mining", ChatFormatting.GREEN, "message.text.mode.silk"), false);
                } else {
                    enchMap.put(Enchantments.BLOCK_FORTUNE, 3);
                    player.displayClientMessage(Helpers.formatComplexMessage(ChatFormatting.LIGHT_PURPLE, "message.text.mode.mining", ChatFormatting.AQUA, "message.text.mode.fortune"), false);
                }
                EnchantmentHelper.setEnchantments(enchMap, stack);
            }
            return InteractionResultHolder.success(stack);
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Player player = context.getPlayer();
        Direction face = context.getClickedFace();
        if (!world.isClientSide() && !player.isCrouching() && !KeyboardHandler.instance.isAltKeyDown(player) && !KeyboardHandler.instance.isModeSwitchKeyDown(player)) {
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
        return hasEnergy(stack) ? 24.0F : 1.0F;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || VALID_ENCHANTS.contains(enchantment);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) ? getEfficiency(stack) : 1.0F;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return DEFAULT_TOOL_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_AXE) || MATERIALS.contains(state.getMaterial())) {
            return TierSortingRegistry.isCorrectTierForDrops(Tiers.NETHERITE, state);
        }
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            extractEnergy(stack, ENERGY_PER_USAGE, false);
        }
        return 0;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && !player.getAbilities().instabuild) {
            extractEnergy(stack, ENERGY_PER_USAGE * 2, false);
        }
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (Utils.isServerWorld(worldIn) && state.getDestroySpeed(worldIn, pos) != 0.0F) {
            if (entityLiving instanceof Player player && !player.getAbilities().instabuild) {
                extractEnergy(stack, ENERGY_PER_USAGE, false);
            }
        }
        return true;
    }

    public float getEfficiency(ItemStack stack) {
        return hasEnergy(stack) ? EFFICIENCY : 1.0F;
    }

    public boolean hasEnergy(ItemStack stack) {
        return getEnergyStored(stack) >= ENERGY_PER_USAGE;
    }


    public enum OmniMode {
        NORMAL(ChatFormatting.LIGHT_PURPLE), VEIN(ChatFormatting.BLUE), VEIN_EXTENDED(ChatFormatting.RED);

        private static final OmniMode[] VALUES = values();
        public final ChatFormatting color;
        public final String name;

        OmniMode(ChatFormatting color) {
            this.name = "message.text.mode." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name());
            this.color = color;
        }

        public static OmniMode getFromId(int ID) {
            return VALUES[ID % VALUES.length];
        }
    }

    public enum OmniProps {
        NORMAL(64.0F, 1600, ChatFormatting.BLUE), LOW_POWER(32.0F, 800, ChatFormatting.GREEN),
        FINE(16.0F, 400, ChatFormatting.AQUA);

        private static final OmniProps[] VALUES = values();
        public final String name;
        public final ChatFormatting color;
        public final float efficiency;
        public final int energyCost;

        OmniProps(float efficiency, int energyCost, ChatFormatting color) {
            this.name = "message.text.mode." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name());
            this.color = color;
            this.efficiency = efficiency;
            this.energyCost = energyCost;
        }

        public static OmniProps getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }
}
