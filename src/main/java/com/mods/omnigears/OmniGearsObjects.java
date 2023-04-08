package com.mods.omnigears;

import com.mods.omnigears.items.ItemComponents;
import com.mods.omnigears.items.armors.ItemAdvancedNanoChest;
import com.mods.omnigears.items.armors.ItemAdvancedQuantChest;
import com.mods.omnigears.items.armors.ItemBaseEnergyPack;
import com.mods.omnigears.items.armors.ItemElectricJetpack;
import com.mods.omnigears.items.tools.ItemDrill;
import com.mods.omnigears.items.tools.ItemOmni;
import com.mods.omnigears.items.tools.ItemSaw;
import net.minecraft.resources.ResourceLocation;
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
    public static Item BATPACK, LAPPACK, ADVANCED_LAPPACK, ULTIMATE_LAPPACK;

    // Jetpacks

    public static Item JETPACK, ADVANCED_JETPACK, ADVANCED_NANO, ADVANCED_QUANT;

    public static void init() {
        DRILL = registerItem(new ItemDrill(), "drill");
        DIAMOND_DRILL = registerItem(new ItemDrill(Tiers.DIAMOND, 1000, 40000), "diamond_drill");
        ADVANCED_DRILL = registerItem(new ItemDrill.ItemAdvancedDrill(), "advanced_drill");
        CHAINSAW = registerItem(new ItemSaw(), "chainsaw");
        ADVANCED_CHAINSAW = registerItem(new ItemSaw.ItemAdvancedSaw(), "advanced_chainsaw");
        OMNITOOL = registerItem(new ItemOmni(), "omnitool");
        BATPACK = registerItem(new ItemBaseEnergyPack("bat_pack", 240000, 1000, Rarity.COMMON), "bat_pack");
        LAPPACK = registerItem(new ItemBaseEnergyPack("lappack", 2400000, 5000, Rarity.UNCOMMON), "lappack");
        ADVANCED_LAPPACK = registerItem(new ItemBaseEnergyPack("advanced_lappack", 4000000, 2000, Rarity.UNCOMMON), "advanced_lappack");
        ULTIMATE_LAPPACK = registerItem(new ItemBaseEnergyPack("ultimate_lappack", 40000000, 20000, Rarity.RARE), "ultimate_lappack");
        JETPACK = registerItem(new ItemElectricJetpack("electric_jetpack", 120000, 500, Rarity.COMMON, 28, false, false), "electric_jetpack");
        ADVANCED_JETPACK = registerItem(new ItemElectricJetpack("advanced_jetpack", 4000000, 5000, Rarity.UNCOMMON, 144, true, true), "advanced_jetpack");
        ADVANCED_NANO = registerItem(new ItemAdvancedNanoChest(), "advanced_nano");
        ADVANCED_QUANT = registerItem(new ItemAdvancedQuantChest(), "advanced_quant");

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
