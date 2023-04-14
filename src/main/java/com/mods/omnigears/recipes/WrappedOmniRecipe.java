package com.mods.omnigears.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;

public abstract class WrappedOmniRecipe  implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

    private final ShapedRecipe internal;

    protected WrappedOmniRecipe(ShapedRecipe internal) {
        this.internal = internal;
    }

    public ShapedRecipe getInternal() {
        return internal;
    }

    @Override
    public abstract ItemStack assemble(CraftingContainer inv);

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        return internal.matches(inv, world) && !assemble(inv).isEmpty();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return internal.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem() {
        return internal.getResultItem();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        return internal.getRemainingItems(inv);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return internal.getIngredients();
    }

    @Override
    public boolean isSpecial() {
        return internal.isSpecial();
    }

    @Override
    public String getGroup() {
        return internal.getGroup();
    }

    @Override
    public ItemStack getToastSymbol() {
        return internal.getToastSymbol();
    }

    @Override
    public ResourceLocation getId() {
        return internal.getId();
    }

    @Override
    public int getRecipeWidth() {
        return internal.getRecipeWidth();
    }

    @Override
    public int getRecipeHeight() {
        return internal.getRecipeHeight();
    }

    @Override
    public boolean isIncomplete() {
        return internal.isIncomplete();
    }
}
