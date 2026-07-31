package net.mountainseal.cereibridge.client.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.mountainseal.cereibridge.client.CraftEngineReiBridgeClient;
import net.mountainseal.cereibridge.client.cache.CeBrewingEntry;
import net.mountainseal.cereibridge.client.platform.VersionSupport;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class CeBrewingDisplay extends BasicDisplay implements CeDisplay {
    private final int generation;

    public CeBrewingDisplay(CeBrewingEntry entry, int generation) {
        super(List.of(ingredient(resolveIngredient(entry.ingredientId()))),
                List.of(ingredient(entry.result())));
        this.generation = generation;
    }

    private static ItemStack resolveIngredient(String id) {
        return CraftEngineReiBridgeClient.items().byId(id)
                .map(item -> item.stack().copy())
                .orElseGet(() -> VersionSupport.itemStack(id));
    }

    private static EntryIngredient ingredient(ItemStack stack) {
        return stack.isEmpty() ? EntryIngredient.empty() : EntryIngredient.of(EntryStacks.of(stack));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CeReiPlugin.BREWING;
    }

    @Override
    public int generation() {
        return generation;
    }
}
