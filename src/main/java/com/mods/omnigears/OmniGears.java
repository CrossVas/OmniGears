package com.mods.omnigears;

import com.mods.omnigears.client.JetpackClientHandler;
import com.mods.omnigears.client.OmniSounds;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(Refs.ID)
public class OmniGears {

    public static CreativeModeTab TAB = new OmniGearsTab();

    public OmniGears() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new JetpackClientHandler());
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::register);
        OmniSounds.REGISTRY.register(bus);
    }

    public void register(RegisterEvent e) {
        if (e.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            OmniGearsObjects.init();
        }
    }
}
