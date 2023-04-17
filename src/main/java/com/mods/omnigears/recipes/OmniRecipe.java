package com.mods.omnigears.recipes;

import cofh.lib.util.constants.NBTTags;
import com.google.gson.JsonObject;
import com.mods.omnigears.Helpers;
import com.mods.omnigears.OmniGears;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OmniRecipe extends WrappedOmniRecipe {

    protected OmniRecipe(ShapedRecipe internal) {
        super(internal);
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        if (getResultItem().isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack output = getResultItem().copy();
        List<ItemStack> energyContainers = new ArrayList<>();
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack tagStack = craftingContainer.getItem(i);
            CompoundTag inputTag = Helpers.getCompoundTag(tagStack);
            if (inputTag.get(NBTTags.TAG_ENERGY) != null) {
                energyContainers.add(tagStack);
            }
        }

        if (energyContainers.isEmpty()) {
            return output;
        }

        energyContainers.forEach(input -> {
            output.setTag(input.getTag());
        });

        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegs.OMNI_RECIPE.get();
    }

    public static class WrappedOmniRecipeSerializer<RECIPE extends WrappedOmniRecipe> implements RecipeSerializer<RECIPE> {

        private final Function<ShapedRecipe, RECIPE> wrapper;

        public WrappedOmniRecipeSerializer(Function<ShapedRecipe, RECIPE> wrapper) {
            this.wrapper = wrapper;
        }

        @NotNull
        @Override
        public RECIPE fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            return wrapper.apply(RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json));
        }

        @Override
        public RECIPE fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            try {
                return wrapper.apply(RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer));
            } catch (Exception e) {
                OmniGears.LOGGER.error("Error reading wrapped shaped recipe from packet.", e);
                throw e;
            }
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RECIPE recipe) {
            try {
                RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe.getInternal());
            } catch (Exception e) {
                OmniGears.LOGGER.error("Error writing wrapped shaped recipe to packet.", e);
                throw e;
            }
        }
    }
}
