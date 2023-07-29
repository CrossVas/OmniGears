package com.mods.omnigears;

import com.google.gson.JsonObject;
import com.mods.omnigears.items.armors.intefaces.IMergeCompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class OmniRecipeUpgrade extends ShapedRecipe {

    public static final RecipeSerializer<OmniRecipeUpgrade> SERIALIZER = new Serializer();

    public OmniRecipeUpgrade(ResourceLocation location, String group, int recipeWidth, int recipeHeight, NonNullList<Ingredient> ingredients, ItemStack output) {
        super(location, group, recipeWidth, recipeHeight, ingredients, output);
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        CompoundTag tag = null;

        loop:
        for(int i = 0; i < craftingContainer.getHeight(); i++) {
            for(int j = 0; j < craftingContainer.getWidth(); j++) {
                ItemStack stack = craftingContainer.getItem(i * craftingContainer.getWidth() + j);
                if(stack.hasTag() && stack.getItem() instanceof IMergeCompoundTag) {
                    tag = stack.getTag();
                    break loop;
                }
            }
        }

        if (tag != null) {
            ItemStack output = getResultItem().copy();
            output.getOrCreateTag().merge(tag);
            return output;
        }

        return super.assemble(craftingContainer);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<OmniRecipeUpgrade> {

        @Override
        public OmniRecipeUpgrade fromJson(ResourceLocation recipeId, JsonObject json) {
            ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json);
            return new OmniRecipeUpgrade(recipeId, recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem());
        }

        @Override
        public @Nullable OmniRecipeUpgrade fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);
            return new OmniRecipeUpgrade(recipeId, recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OmniRecipeUpgrade recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
