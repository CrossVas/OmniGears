package com.mods.omnigears.recipes;

import com.mods.omnigears.Refs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegs {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Refs.ID);
    public static final RegistryObject<RecipeSerializer<OmniRecipe>> OMNI_RECIPE = SERIALIZERS.register("omni_recipe", () -> new OmniRecipe.WrappedOmniRecipeSerializer<>(OmniRecipe::new));

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }

}
