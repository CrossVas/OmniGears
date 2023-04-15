package com.mods.omnigears;

import net.minecraftforge.common.ForgeConfigSpec;

public class OmniConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> HUD_POS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SHOW_HUD;

    static {
        BUILDER.push(Refs.ID);
        SHOW_HUD = BUILDER.comment("Show OmniGears overlay.").define("overlay_enabled", true);
        HUD_POS = BUILDER.comment("OmniGears Armor Stats Display Position. 1 - Top Left, 2 - Top Right, 3 - Bottom Left, 4 - Bottom Right").defineInRange("overlay_position", 1, 1, 4);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
