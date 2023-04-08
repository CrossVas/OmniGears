package com.mods.omnigears.client;

import com.mods.omnigears.Refs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OmniSounds {

    public static final String JETPACK_SOUND_ID = "jetpack_sound";
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Refs.ID);
    public static RegistryObject<SoundEvent> JETPACK_SOUND = registerSounds(JETPACK_SOUND_ID);

    public static RegistryObject<SoundEvent> registerSounds(String name) {
        return REGISTRY.register(name, () -> new SoundEvent(new ResourceLocation(Refs.ID, name)));
    }
}
