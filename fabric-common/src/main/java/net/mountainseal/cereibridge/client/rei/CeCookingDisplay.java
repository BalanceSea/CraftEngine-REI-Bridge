package net.mountainseal.cereibridge.client.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.mountainseal.cereibridge.client.cache.CeCookingEntry;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class CeCookingDisplay extends BasicDisplay implements CeDisplay {
    private final CategoryIdentifier<CeCookingDisplay> categoryIdentifier;
    private final float experience;
    private final int cookingTime;
    private final int generation;

    public CeCookingDisplay(CategoryIdentifier<CeCookingDisplay> categoryIdentifier,
                            CeCookingEntry entry, int generation) {
        super(List.of(ingredient(entry.ingredient())), List.of(ingredient(entry.result())));
        this.categoryIdentifier = categoryIdentifier;
        this.experience = entry.experience();
        this.cookingTime = entry.cookingTime();
        this.generation = generation;
    }

    private static EntryIngredient ingredient(ItemStack stack) {
        return stack.isEmpty() ? EntryIngredient.empty() : EntryIngredient.of(EntryStacks.of(stack));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return categoryIdentifier;
    }

    public float experience() {
        return experience;
    }

    public int cookingTime() {
        return cookingTime;
    }

    @Override
    public int generation() {
        return generation;
    }
}
