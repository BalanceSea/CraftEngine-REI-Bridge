package net.mountainseal.cereibridge.client.rei;

import dev.architectury.event.EventResult;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.mountainseal.cereibridge.client.CraftEngineReiBridgeClient;
import net.mountainseal.cereibridge.client.cache.CeItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CeReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<CeCraftingDisplay> CRAFTING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "crafting");
    public static final CategoryIdentifier<CeSmithingDisplay> SMITHING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "smithing");
    public static final CategoryIdentifier<CeBrewingDisplay> BREWING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "brewing");

    private static EntryRegistry entryRegistry;
    private static DisplayRegistry displayRegistry;
    private static List<EntryStack<?>> registeredItems = List.of();
    private static int craftingGeneration;
    private static int smithingGeneration;
    private static int brewingGeneration;

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CeCraftingCategory());
        registry.add(new CeSmithingCategory());
        registry.add(new CeBrewingCategory());
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        entryRegistry = registry;
        replaceItems();
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        displayRegistry = registry;
        registry.registerVisibilityPredicate((category, display) -> {
            if (display instanceof CeCraftingDisplay crafting && crafting.generation() != craftingGeneration) {
                return EventResult.interruptFalse();
            }
            if (display instanceof CeSmithingDisplay smithing && smithing.generation() != smithingGeneration) {
                return EventResult.interruptFalse();
            }
            if (display instanceof CeBrewingDisplay brewing && brewing.generation() != brewingGeneration) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        addCraftingDisplays();
        addSmithingDisplays();
        addBrewingDisplays();
    }

    public static void onItemsUpdated() {
        replaceItems();
    }

    public static void onCraftingUpdated() {
        craftingGeneration++;
        addCraftingDisplays();
    }

    public static void onSmithingUpdated() {
        smithingGeneration++;
        addSmithingDisplays();
    }

    public static void onBrewingUpdated() {
        brewingGeneration++;
        addBrewingDisplays();
    }

    private static void replaceItems() {
        if (entryRegistry == null) {
            return;
        }
        for (EntryStack<?> stack : registeredItems) {
            entryRegistry.removeEntry(stack);
        }
        List<EntryStack<?>> fresh = new ArrayList<>();
        for (CeItem item : CraftEngineReiBridgeClient.items().all()) {
            ItemStack stack = item.stack();
            if (!stack.isEmpty()) {
                fresh.add(EntryStacks.of(stack));
            }
        }
        if (!fresh.isEmpty()) {
            entryRegistry.addEntries(fresh);
        }
        registeredItems = List.copyOf(fresh);
        entryRegistry.refilter();
    }

    private static void addCraftingDisplays() {
        if (displayRegistry == null) {
            return;
        }
        CraftEngineReiBridgeClient.crafting().all().forEach(entry ->
                displayRegistry.add(new CeCraftingDisplay(entry, craftingGeneration)));
    }

    private static void addSmithingDisplays() {
        if (displayRegistry == null) {
            return;
        }
        CraftEngineReiBridgeClient.smithing().all().forEach(entry ->
                displayRegistry.add(new CeSmithingDisplay(entry, smithingGeneration)));
    }

    private static void addBrewingDisplays() {
        if (displayRegistry == null) {
            return;
        }
        CraftEngineReiBridgeClient.brewing().all().forEach(entry ->
                displayRegistry.add(new CeBrewingDisplay(entry, brewingGeneration)));
    }
}
