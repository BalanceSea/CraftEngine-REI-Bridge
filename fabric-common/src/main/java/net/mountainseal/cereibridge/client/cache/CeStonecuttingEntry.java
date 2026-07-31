package net.mountainseal.cereibridge.client.cache;

import net.minecraft.world.item.ItemStack;

public record CeStonecuttingEntry(String recipeId, ItemStack ingredient, ItemStack result) {
}
