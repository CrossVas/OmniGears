package com.mods.omnigears;

import com.mods.omnigears.items.ItemComponents;
import com.mods.omnigears.items.armors.*;
import com.mods.omnigears.items.tools.ItemDrill;
import com.mods.omnigears.items.tools.ItemOmni;
import com.mods.omnigears.items.tools.ItemSaw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.ForgeRegistries;

public class OmniGearsObjects {

    // Components
    public static Item COOLING_CORE, ENGINE_BOOSTER, GRAVI_ENGINE, MAGNETRON, SUPERCONDUCTOR, SUPERCONDUCTOR_COVER, OMNI_CORE;

    // Tools
    public static Item DRILL, DIAMOND_DRILL, ADVANCED_DRILL, CHAINSAW, ADVANCED_CHAINSAW, OMNITOOL;

    // Energy Packs
    public static Item ENERGY_PACK, LAZULI_PACK, ADVANCED_LAZULI_PACK, ULTIMATE_LAZULI_PACK;

    // Jetpacks
    public static Item JETPACK, ADVANCED_JETPACK, ADVANCED_JETPACK_CHEST, ADVANCED_OMNI_ARMOR;

    // Armor set
    public static Item ADV_HELMET, ADV_CHEST, ADV_LEGS, ADV_BOOTS;

    public static void init() {
        DRILL = registerItem(new ItemDrill(), "drill");
        DIAMOND_DRILL = registerItem(new ItemDrill(Tiers.DIAMOND, 1000, 40000), "diamond_drill");
        ADVANCED_DRILL = registerItem(new ItemDrill.ItemAdvancedDrill(), "advanced_drill");
        CHAINSAW = registerItem(new ItemSaw(), "chainsaw");
        ADVANCED_CHAINSAW = registerItem(new ItemSaw.ItemAdvancedSaw(), "advanced_chainsaw");
        OMNITOOL = registerItem(new ItemOmni(), "omnitool");
        ENERGY_PACK = registerItem(new ItemBaseEnergyPack("energy_pack", 240000, 1000, Rarity.COMMON), "energy_pack");
        LAZULI_PACK = registerItem(new ItemBaseEnergyPack("lazuli_pack", 2400000, 5000, Rarity.UNCOMMON), "lazuli_pack");
        ADVANCED_LAZULI_PACK = registerItem(new ItemBaseEnergyPack("advanced_lazuli_pack", 4000000, 2000, Rarity.UNCOMMON), "advanced_lazuli_pack");
        ULTIMATE_LAZULI_PACK = registerItem(new ItemBaseEnergyPack("ultimate_lazuli_pack", 40000000, 20000, Rarity.RARE), "ultimate_lazuli_pack");
        JETPACK = registerItem(new ItemElectricJetpack("electric_jetpack", 120000, 500, Rarity.COMMON, 28, false, false), "electric_jetpack");
        ADVANCED_JETPACK = registerItem(new ItemElectricJetpack("advanced_jetpack", 4000000, 5000, Rarity.UNCOMMON, 144, true, true), "advanced_jetpack");
        ADVANCED_JETPACK_CHEST = registerItem(new ItemAdvancedJetpackChest(), "advanced_jetpack_chest");
        ADVANCED_OMNI_ARMOR = registerItem(new ItemAdvancedOmniArmor(), "advanced_omniarmor");

        ADV_HELMET = registerItem(new ItemArmorAdvanced(EquipmentSlot.HEAD), "adv_helmet");
        ADV_CHEST = registerItem(new ItemArmorAdvanced(EquipmentSlot.CHEST), "adv_chest");
        ADV_LEGS = registerItem(new ItemArmorAdvanced(EquipmentSlot.LEGS), "adv_legs");
        ADV_BOOTS = registerItem(new ItemArmorAdvanced(EquipmentSlot.FEET), "adv_boots");

        COOLING_CORE = registerItem(new ItemComponents(), "cooling_core");
        ENGINE_BOOSTER = registerItem(new ItemComponents(), "engine_booster");
        GRAVI_ENGINE = registerItem(new ItemComponents(), "gravi_engine");
        MAGNETRON = registerItem(new ItemComponents(), "magnetron");
        SUPERCONDUCTOR = registerItem(new ItemComponents(), "superconductor");
        SUPERCONDUCTOR_COVER = registerItem(new ItemComponents(), "superconductor_cover");
        OMNI_CORE = registerItem(new ItemComponents(), "omni_core");
    }

    public static <T extends Item> T registerItem(T item, String name) {
        ResourceLocation id = new ResourceLocation(Refs.ID, name);
        ForgeRegistries.ITEMS.register(id, item);
        return item;
    }


}
