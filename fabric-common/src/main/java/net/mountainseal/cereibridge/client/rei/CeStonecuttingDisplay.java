package net.mountainseal.cereibridge.client.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.mountainseal.cereibridge.client.cache.CeStonecuttingEntry;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class CeStonecuttingDisplay extends BasicDisplay implements CeDisplay {
    private final int generation;

    public CeStonecuttingDisplay(CeStonecuttingEntry entry, int generation) {
        super(List.of(ingredient(entry.ingredient())), List.of(ingredient(entry.result())));
        this.generation = generation;
    }

    private static EntryIngredient ingredient(ItemStack stack) {
        return stack.isEmpty() ? EntryIngredient.empty() : EntryIngredient.of(EntryStacks.of(stack));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CeReiPlugin.STONECUTTING;
    }

    @Override
    public int generation() {
        return generation;
    }
}
