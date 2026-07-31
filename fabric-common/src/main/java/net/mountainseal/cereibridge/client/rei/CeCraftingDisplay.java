package net.mountainseal.cereibridge.client.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.mountainseal.cereibridge.client.cache.CeCraftingEntry;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class CeCraftingDisplay extends BasicDisplay implements CeDisplay {
    private final boolean shapeless;
    private final int width;
    private final int height;
    private final int generation;

    public CeCraftingDisplay(CeCraftingEntry entry, int generation) {
        super(entry.inputs().stream().map(CeCraftingDisplay::ingredient).toList(),
                List.of(ingredient(entry.result())));
        this.shapeless = entry.shapeless();
        this.width = entry.width();
        this.height = entry.height();
        this.generation = generation;
    }

    private static EntryIngredient ingredient(ItemStack stack) {
        return stack.isEmpty() ? EntryIngredient.empty() : EntryIngredient.of(EntryStacks.of(stack));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CeReiPlugin.CRAFTING;
    }

    public boolean shapeless() {
        return shapeless;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    public int generation() {
        return generation;
    }
}
