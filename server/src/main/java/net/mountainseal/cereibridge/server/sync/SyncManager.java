package net.mountainseal.cereibridge.server.sync;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.bukkit.item.BukkitItemManager;
import net.momirealms.craftengine.bukkit.item.recipe.BukkitRecipeManager;
import net.momirealms.craftengine.core.item.Item;
import net.momirealms.craftengine.core.item.ItemBuildContext;
import net.momirealms.craftengine.core.item.ItemDefinition;
import net.momirealms.craftengine.core.item.recipe.CustomBrewingRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomCraftingTableRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomShapedRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomShapelessRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomSmithingTransformRecipe;
import net.momirealms.craftengine.core.item.recipe.Ingredient;
import net.momirealms.craftengine.core.item.recipe.Recipe;
import net.momirealms.craftengine.core.item.recipe.RecipeType;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.UniqueKey;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class SyncManager {
    private final JavaPlugin plugin;
    private volatile byte[] itemsPayload = emptyPayload();
    private volatile byte[] craftingPayload = emptyPayload();
    private volatile byte[] smithingPayload = emptyPayload();
    private volatile byte[] brewingPayload = emptyPayload();

    public SyncManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public byte[] itemsPayload() {
        return itemsPayload;
    }

    public byte[] craftingPayload() {
        return craftingPayload;
    }

    public byte[] smithingPayload() {
        return smithingPayload;
    }

    public byte[] brewingPayload() {
        return brewingPayload;
    }

    public void rebuild() {
        itemsPayload = buildItemsPayload();
        craftingPayload = buildCraftingPayload();
        smithingPayload = buildSmithingPayload();
        brewingPayload = buildBrewingPayload();
        plugin.getLogger().info(describe());
    }

    public String describe() {
        return "CraftEngine REI sync: items=" + itemsPayload.length + "B, crafting="
                + craftingPayload.length + "B, smithing=" + smithingPayload.length
                + "B, brewing=" + brewingPayload.length + "B";
    }

    private byte[] buildItemsPayload() {
        Map<Key, ItemDefinition> loaded;
        try {
            loaded = CraftEngineItems.loadedItems();
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Failed to read CraftEngine items", throwable);
            return emptyPayload();
        }

        List<byte[]> entries = new ArrayList<>();
        for (Key id : loaded.keySet()) {
            try {
                BukkitItemDefinition definition = CraftEngineItems.byId(id);
                if (definition == null) {
                    continue;
                }
                ItemStack stack = definition.buildItem(ItemBuildContext.empty(), 1).getBukkitItem();
                if (isEmpty(stack)) {
                    continue;
                }
                stack = toClientBoundStack(stack);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream output = new DataOutputStream(bytes);
                output.writeUTF(id.asString());
                writeItemAppearance(output, stack, id.asString());
                entries.add(bytes.toByteArray());
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.WARNING, "Failed to export CraftEngine item '" + id + "'", throwable);
            }
        }
        return countPrefixed(entries);
    }

    private byte[] buildCraftingPayload() {
        List<byte[]> entries = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();

        try {
            for (Recipe recipe : BukkitRecipeManager.instance().recipesByType(RecipeType.CRAFTING)) {
                try {
                    byte[] entry = buildCraftEngineCraftingEntry(recipe);
                    if (entry != null) {
                        entries.add(entry);
                        seenIds.add(recipe.id().asString());
                    }
                } catch (Throwable throwable) {
                    plugin.getLogger().log(Level.WARNING,
                            "Failed to export CraftEngine crafting recipe '" + recipe.id() + "'", throwable);
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Failed to read CraftEngine crafting recipes", throwable);
        }

        var iterator = plugin.getServer().recipeIterator();
        while (iterator.hasNext()) {
            org.bukkit.inventory.Recipe recipe = iterator.next();
            try {
                byte[] entry = buildBukkitCraftingEntry(recipe, seenIds);
                if (entry != null) {
                    entries.add(entry);
                    if (recipe instanceof Keyed keyed) {
                        seenIds.add(keyed.getKey().toString());
                    }
                }
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.WARNING, "Failed to export a Bukkit crafting recipe", throwable);
            }
        }
        return countPrefixed(entries);
    }

    private byte[] buildCraftEngineCraftingEntry(Recipe recipe) throws IOException {
        if (!(recipe instanceof CustomCraftingTableRecipe crafting)) {
            return null;
        }

        boolean shapeless;
        int width;
        int height;
        Ingredient[] grid;
        if (crafting instanceof CustomShapedRecipe shaped) {
            shapeless = false;
            var pattern = shaped.parsedPattern();
            width = pattern.width();
            height = pattern.height();
            if (width < 1 || height < 1 || width > 3 || height > 3) {
                return null;
            }
            var ingredients = pattern.ingredients();
            grid = new Ingredient[ingredients.length];
            for (int index = 0; index < ingredients.length; index++) {
                grid[index] = ingredients[index].orElse(null);
            }
        } else if (crafting instanceof CustomShapelessRecipe recipeValue) {
            shapeless = true;
            width = 3;
            height = 3;
            grid = new Ingredient[9];
            List<Ingredient> ingredients = recipeValue.ingredientsInUse();
            for (int index = 0; index < ingredients.size() && index < grid.length; index++) {
                grid[index] = ingredients.get(index);
            }
        } else {
            return null;
        }

        Item result = crafting.buildVisualOrActualResult(ItemBuildContext.empty());
        if (result == null || !(result.platformItem() instanceof ItemStack resultStack) || isEmpty(resultStack)) {
            return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeUTF(recipe.id().asString());
        output.writeBoolean(shapeless);
        output.writeByte(width);
        output.writeByte(height);
        for (Ingredient ingredient : grid) {
            ItemStack stack = representativeStack(ingredient);
            output.writeBoolean(stack != null);
            if (stack != null) {
                String identity = identityOf(stack);
                writeItemAppearance(output, toClientBoundStack(stack), identity);
            }
        }
        String resultIdentity = result.getDefinition().map(definition -> definition.id().asString()).orElse("");
        resultStack = toClientBoundStack(resultStack);
        writeItemAppearance(output, resultStack, resultIdentity);
        return bytes.toByteArray();
    }

    private byte[] buildBukkitCraftingEntry(org.bukkit.inventory.Recipe recipe, Set<String> seenIds) throws IOException {
        if (!(recipe instanceof Keyed keyed) || seenIds.contains(keyed.getKey().toString())) {
            return null;
        }

        boolean shapeless;
        int width;
        int height;
        ItemStack[] grid;
        if (recipe instanceof org.bukkit.inventory.ShapedRecipe shaped) {
            shapeless = false;
            String[] shape = shaped.getShape();
            height = shape.length;
            width = height == 0 ? 0 : shape[0].length();
            if (width < 1 || height < 1 || width > 3 || height > 3) {
                return null;
            }
            grid = new ItemStack[width * height];
            Map<Character, RecipeChoice> choices = shaped.getChoiceMap();
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    char symbol = column < shape[row].length() ? shape[row].charAt(column) : ' ';
                    RecipeChoice choice = symbol == ' ' ? null : choices.get(symbol);
                    grid[row * width + column] = firstChoice(choice);
                }
            }
        } else if (recipe instanceof org.bukkit.inventory.ShapelessRecipe recipeValue) {
            shapeless = true;
            width = 3;
            height = 3;
            grid = new ItemStack[9];
            List<RecipeChoice> choices = recipeValue.getChoiceList();
            for (int index = 0; index < choices.size() && index < grid.length; index++) {
                grid[index] = firstChoice(choices.get(index));
            }
        } else {
            return null;
        }

        ItemStack result = recipe.getResult();
        if (isEmpty(result) || !involvesCraftEngine(result, grid)) {
            return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeUTF(keyed.getKey().toString());
        output.writeBoolean(shapeless);
        output.writeByte(width);
        output.writeByte(height);
        for (ItemStack stack : grid) {
            output.writeBoolean(!isEmpty(stack));
            if (!isEmpty(stack)) {
                String identity = identityOf(stack);
                ItemStack converted = toClientBoundStack(stack);
                writeItemAppearance(output, converted, identity);
            }
        }
        String resultIdentity = identityOf(result);
        ItemStack convertedResult = toClientBoundStack(result);
        writeItemAppearance(output, convertedResult, resultIdentity);
        return bytes.toByteArray();
    }

    private byte[] buildSmithingPayload() {
        List<byte[]> entries = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        try {
            for (Recipe recipe : BukkitRecipeManager.instance().recipesByType(RecipeType.SMITHING)) {
                try {
                    byte[] entry = buildCraftEngineSmithingEntry(recipe);
                    if (entry != null) {
                        entries.add(entry);
                        seenIds.add(recipe.id().asString());
                    }
                } catch (Throwable throwable) {
                    plugin.getLogger().log(Level.WARNING,
                            "Failed to export CraftEngine smithing recipe '" + recipe.id() + "'", throwable);
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Failed to read CraftEngine smithing recipes", throwable);
        }

        var iterator = plugin.getServer().recipeIterator();
        while (iterator.hasNext()) {
            org.bukkit.inventory.Recipe recipe = iterator.next();
            try {
                byte[] entry = buildBukkitSmithingEntry(recipe, seenIds);
                if (entry != null) {
                    entries.add(entry);
                }
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.WARNING, "Failed to export a Bukkit smithing recipe", throwable);
            }
        }
        return countPrefixed(entries);
    }

    private byte[] buildCraftEngineSmithingEntry(Recipe recipe) throws IOException {
        if (!(recipe instanceof CustomSmithingTransformRecipe smithing)) {
            return null;
        }
        Item result = smithing.buildVisualOrActualResult(ItemBuildContext.empty());
        if (result == null || !(result.platformItem() instanceof ItemStack resultStack) || isEmpty(resultStack)) {
            return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeUTF(recipe.id().asString());
        writeOptionalAppearance(output, representativeStack(smithing.template()));
        writeOptionalAppearance(output, representativeStack(smithing.base()));
        writeOptionalAppearance(output, representativeStack(smithing.addition()));
        String resultIdentity = result.getDefinition().map(definition -> definition.id().asString()).orElse("");
        ItemStack converted = toClientBoundStack(resultStack);
        writeItemAppearance(output, converted, resultIdentity);
        return bytes.toByteArray();
    }

    private byte[] buildBukkitSmithingEntry(org.bukkit.inventory.Recipe recipe, Set<String> seenIds) throws IOException {
        if (!(recipe instanceof org.bukkit.inventory.SmithingTransformRecipe smithing)) {
            return null;
        }
        String recipeId = smithing.getKey().toString();
        if (seenIds.contains(recipeId)) {
            return null;
        }
        ItemStack template = firstChoice(smithing.getTemplate());
        ItemStack base = firstChoice(smithing.getBase());
        ItemStack addition = firstChoice(smithing.getAddition());
        ItemStack result = smithing.getResult();
        if (isEmpty(result) || !(isCraftEngineItem(result) || isCraftEngineItem(template)
                || isCraftEngineItem(base) || isCraftEngineItem(addition))) {
            return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeUTF(recipeId);
        writeOptionalAppearance(output, template);
        writeOptionalAppearance(output, base);
        writeOptionalAppearance(output, addition);
        String resultIdentity = identityOf(result);
        ItemStack converted = toClientBoundStack(result);
        writeItemAppearance(output, converted, resultIdentity);
        return bytes.toByteArray();
    }

    private byte[] buildBrewingPayload() {
        List<byte[]> entries = new ArrayList<>();
        try {
            for (Recipe recipe : BukkitRecipeManager.instance().recipesByType(RecipeType.BREWING)) {
                if (!(recipe instanceof CustomBrewingRecipe brewing)) {
                    continue;
                }
                try {
                    String ingredientId = representativeId(brewing.ingredient());
                    if (ingredientId == null) {
                        continue;
                    }
                    Object platformResult = brewing.result().buildItem(ItemBuildContext.empty()).platformItem();
                    if (!(platformResult instanceof ItemStack result) || isEmpty(result)) {
                        continue;
                    }
                    String resultIdentity = identityOf(result);
                    ItemStack converted = toClientBoundStack(result);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    DataOutputStream output = new DataOutputStream(bytes);
                    output.writeUTF(recipe.id().asString());
                    output.writeUTF(ingredientId);
                    writeItemAppearance(output, converted, resultIdentity);
                    entries.add(bytes.toByteArray());
                } catch (Throwable throwable) {
                    plugin.getLogger().log(Level.WARNING,
                            "Failed to export CraftEngine brewing recipe '" + recipe.id() + "'", throwable);
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Failed to read CraftEngine brewing recipes", throwable);
        }
        return countPrefixed(entries);
    }

    private static ItemStack representativeStack(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }
        for (UniqueKey key : ingredient.items()) {
            BukkitItemDefinition definition = CraftEngineItems.byId(key.key());
            if (definition != null) {
                ItemStack stack = definition.buildItem(ItemBuildContext.empty(), 1).getBukkitItem();
                return isEmpty(stack) ? null : stack;
            }
        }
        for (UniqueKey key : ingredient.minecraftItems()) {
            Material material = Material.matchMaterial(key.key().asString());
            if (material != null && !material.isAir()) {
                return new ItemStack(material);
            }
        }
        return null;
    }

    private static String representativeId(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }
        if (!ingredient.items().isEmpty()) {
            return ingredient.items().get(0).key().asString();
        }
        if (!ingredient.minecraftItems().isEmpty()) {
            return ingredient.minecraftItems().get(0).key().asString();
        }
        return null;
    }

    private static ItemStack firstChoice(RecipeChoice choice) {
        if (choice == null) {
            return null;
        }
        ItemStack stack = choice.getItemStack();
        return isEmpty(stack) ? null : stack;
    }

    private static boolean involvesCraftEngine(ItemStack result, ItemStack[] ingredients) {
        if (isCraftEngineItem(result)) {
            return true;
        }
        for (ItemStack ingredient : ingredients) {
            if (isCraftEngineItem(ingredient)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCraftEngineItem(ItemStack stack) {
        return !isEmpty(stack) && BukkitItemManager.instance().wrap(stack).getDefinition().isPresent();
    }

    private static String identityOf(ItemStack stack) {
        if (isEmpty(stack)) {
            return "";
        }
        return BukkitItemManager.instance().wrap(stack).getDefinition()
                .map(definition -> definition.id().asString())
                .orElse("");
    }

    private static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType().isAir();
    }

    private static void writeOptionalAppearance(DataOutputStream output, ItemStack stack) throws IOException {
        output.writeBoolean(!isEmpty(stack));
        if (!isEmpty(stack)) {
            String identity = identityOf(stack);
            ItemStack converted = toClientBoundStack(stack);
            writeItemAppearance(output, converted, identity);
        }
    }

    public static ItemStack toClientBoundStack(ItemStack stack) {
        return BukkitItemManager.instance().s2c(stack, null).orElse(stack);
    }

    private static void writeItemAppearance(DataOutputStream output, ItemStack stack, String identityId)
            throws IOException {
        output.writeUTF(stack.getType().getKey().toString());
        ItemMeta meta = stack.getItemMeta();

        boolean hasCustomModelData = meta != null && meta.hasCustomModelData();
        output.writeBoolean(hasCustomModelData);
        if (hasCustomModelData) {
            output.writeInt(meta.getCustomModelData());
        }

        boolean hasItemModel = meta != null && meta.hasItemModel();
        output.writeBoolean(hasItemModel);
        if (hasItemModel) {
            output.writeUTF(meta.getItemModel().toString());
        }

        Component name = null;
        if (meta != null) {
            if (meta.hasCustomName()) {
                name = meta.customName();
            } else if (meta.hasItemName()) {
                name = meta.itemName();
            }
        }
        output.writeBoolean(name != null);
        if (name != null) {
            output.writeUTF(GsonComponentSerializer.gson().serialize(name));
        }
        output.writeUTF(identityId == null ? "" : identityId);
    }

    private static byte[] countPrefixed(List<byte[]> entries) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(bytes);
            output.writeInt(entries.size());
            for (byte[] entry : entries) {
                output.write(entry);
            }
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to build a bridge payload", exception);
        }
    }

    private static byte[] emptyPayload() {
        return countPrefixed(List.of());
    }
}
