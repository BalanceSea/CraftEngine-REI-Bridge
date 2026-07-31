package net.mountainseal.cereibridge.client.cache;

import net.minecraft.world.item.ItemStack;

public record CeBrewingEntry(String recipeId, String ingredientId, ItemStack result) {
}
