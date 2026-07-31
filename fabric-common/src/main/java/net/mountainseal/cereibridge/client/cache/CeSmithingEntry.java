package net.mountainseal.cereibridge.client.cache;

import net.minecraft.world.item.ItemStack;

public record CeSmithingEntry(
        String recipeId,
        ItemStack template,
        ItemStack base,
        ItemStack addition,
        ItemStack result
) {
}
