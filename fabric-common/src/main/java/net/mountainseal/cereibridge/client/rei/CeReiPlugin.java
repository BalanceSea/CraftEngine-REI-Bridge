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
import net.mountainseal.cereibridge.client.cache.CeCookingEntry;
import net.mountainseal.cereibridge.client.cache.CeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CeReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<CeCraftingDisplay> CRAFTING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "crafting");
    public static final CategoryIdentifier<CeSmithingDisplay> SMITHING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "smithing");
    public static final CategoryIdentifier<CeBrewingDisplay> BREWING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "brewing");
    public static final CategoryIdentifier<CeCookingDisplay> SMELTING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "smelting");
    public static final CategoryIdentifier<CeCookingDisplay> BLASTING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "blasting");
    public static final CategoryIdentifier<CeCookingDisplay> SMOKING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "smoking");
    public static final CategoryIdentifier<CeCookingDisplay> CAMPFIRE =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "campfire");
    public static final CategoryIdentifier<CeStonecuttingDisplay> STONECUTTING =
            CategoryIdentifier.of(CraftEngineReiBridgeClient.MOD_ID, "stonecutting");

    private static EntryRegistry entryRegistry;
    private static DisplayRegistry displayRegistry;
    private static List<EntryStack<?>> registeredItems = List.of();
    private static int craftingGeneration;
    private static int smithingGeneration;
    private static int brewingGeneration;
    private static int smeltingGeneration;
    private static int blastingGeneration;
    private static int smokingGeneration;
    private static int campfireGeneration;
    private static int stonecuttingGeneration;

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CeCraftingCategory());
        registry.add(new CeSmithingCategory());
        registry.add(new CeBrewingCategory());
        registry.add(new CeCookingCategory(SMELTING,
                "category.craftengine_rei_bridge.smelting", Items.FURNACE, 200, true));
        registry.add(new CeCookingCategory(BLASTING,
                "category.craftengine_rei_bridge.blasting", Items.BLAST_FURNACE, 100, true));
        registry.add(new CeCookingCategory(SMOKING,
                "category.craftengine_rei_bridge.smoking", Items.SMOKER, 100, true));
        registry.add(new CeCookingCategory(CAMPFIRE,
                "category.craftengine_rei_bridge.campfire", Items.CAMPFIRE, 600, false));
        registry.add(new CeStonecuttingCategory());
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
            if (display instanceof CeCookingDisplay cooking
                    && cooking.generation() != cookingGeneration(cooking.getCategoryIdentifier())) {
                return EventResult.interruptFalse();
            }
            if (display instanceof CeStonecuttingDisplay stonecutting
                    && stonecutting.generation() != stonecuttingGeneration) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        addCraftingDisplays();
        addSmithingDisplays();
        addBrewingDisplays();
        addSmeltingDisplays();
        addBlastingDisplays();
        addSmokingDisplays();
        addCampfireDisplays();
        addStonecuttingDisplays();
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

    public static void onSmeltingUpdated() {
        smeltingGeneration++;
        addSmeltingDisplays();
    }

    public static void onBlastingUpdated() {
        blastingGeneration++;
        addBlastingDisplays();
    }

    public static void onSmokingUpdated() {
        smokingGeneration++;
        addSmokingDisplays();
    }

    public static void onCampfireUpdated() {
        campfireGeneration++;
        addCampfireDisplays();
    }

    public static void onStonecuttingUpdated() {
        stonecuttingGeneration++;
        addStonecuttingDisplays();
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

    private static void addSmeltingDisplays() {
        addCookingDisplays(SMELTING, CraftEngineReiBridgeClient.smelting().all(), smeltingGeneration);
    }

    private static void addBlastingDisplays() {
        addCookingDisplays(BLASTING, CraftEngineReiBridgeClient.blasting().all(), blastingGeneration);
    }

    private static void addSmokingDisplays() {
        addCookingDisplays(SMOKING, CraftEngineReiBridgeClient.smoking().all(), smokingGeneration);
    }

    private static void addCampfireDisplays() {
        addCookingDisplays(CAMPFIRE, CraftEngineReiBridgeClient.campfire().all(), campfireGeneration);
    }

    private static void addCookingDisplays(CategoryIdentifier<CeCookingDisplay> category,
                                           Collection<CeCookingEntry> entries, int generation) {
        if (displayRegistry == null) {
            return;
        }
        entries.forEach(entry -> displayRegistry.add(new CeCookingDisplay(category, entry, generation)));
    }

    private static void addStonecuttingDisplays() {
        if (displayRegistry == null) {
            return;
        }
        CraftEngineReiBridgeClient.stonecutting().all().forEach(entry ->
                displayRegistry.add(new CeStonecuttingDisplay(entry, stonecuttingGeneration)));
    }

    private static int cookingGeneration(CategoryIdentifier<?> category) {
        if (SMELTING.equals(category)) {
            return smeltingGeneration;
        }
        if (BLASTING.equals(category)) {
            return blastingGeneration;
        }
        if (SMOKING.equals(category)) {
            return smokingGeneration;
        }
        if (CAMPFIRE.equals(category)) {
            return campfireGeneration;
        }
        return -1;
    }
}
