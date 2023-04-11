package com.mods.omnigears;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class OmniGearsTab extends CreativeModeTab {

    public OmniGearsTab() {
        super(Refs.ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(OmniGearsObjects.ADVANCED_OMNI_ARMOR);
    }
}
