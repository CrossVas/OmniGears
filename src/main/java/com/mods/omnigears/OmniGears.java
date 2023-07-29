package com.mods.omnigears;

import com.mods.omnigears.client.JetpackClientHandler;
import com.mods.omnigears.client.OmniOverlay;
import com.mods.omnigears.client.OmniSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.Objects;

@Mod(Refs.ID)
public class OmniGears {

    public static CreativeModeTab TAB = new OmniGearsTab();
    public static final Logger LOGGER = LogUtils.getLogger();

    public OmniGears() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(new JetpackClientHandler());
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OmniConfig.SPEC, Refs.ID + "-client.toml");
        OmniSounds.REGISTRY.register(bus);
    }



    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onRegisterEvent(RegisterEvent e) {
            if (e.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS))
                OmniGearsObjects.init();
            else if (e.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
                onRecipeRegistry(Objects.requireNonNull(e.getForgeRegistry()));
        }

        public static void onRecipeRegistry(IForgeRegistry<RecipeSerializer<?>> registry) {
            registry.register("omni_recipe", OmniRecipeUpgrade.SERIALIZER);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerOverlay(RegisterGuiOverlaysEvent e) {
        e.registerBelowAll("omni_overlay", new OmniOverlay(Minecraft.getInstance()));
    }
 }
