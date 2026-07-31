package net.mountainseal.cereibridge.client.cache;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record CeCraftingEntry(
        String recipeId,
        boolean shapeless,
        int width,
        int height,
        List<ItemStack> inputs,
        ItemStack result
) {
}
