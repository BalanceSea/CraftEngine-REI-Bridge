package net.mountainseal.cereibridge.client.cache;

import net.minecraft.world.item.ItemStack;

public record CeCookingEntry(
        String recipeId,
        ItemStack ingredient,
        ItemStack result,
        float experience,
        int cookingTime
) {
}
