package net.mountainseal.cereibridge.client.cache;

import net.mountainseal.cereibridge.client.platform.VersionSupport;
import net.minecraft.world.item.ItemStack;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CeCraftingRegistry {
    private final List<CeCraftingEntry> entries = new ArrayList<>();

    public void readFrom(DataInputStream input) throws IOException {
        List<CeCraftingEntry> fresh = new ArrayList<>();
        int count = input.readInt();
        if (count < 0 || count > 100_000) {
            throw new IOException("Invalid crafting recipe count: " + count);
        }
        for (int index = 0; index < count; index++) {
            String recipeId = input.readUTF();
            boolean shapeless = input.readBoolean();
            int width = input.readUnsignedByte();
            int height = input.readUnsignedByte();
            if (width < 1 || width > 3 || height < 1 || height > 3) {
                throw new IOException("Invalid crafting dimensions for " + recipeId + ": " + width + "x" + height);
            }
            List<ItemStack> ingredients = new ArrayList<>(width * height);
            for (int slot = 0; slot < width * height; slot++) {
                ingredients.add(input.readBoolean()
                        ? VersionSupport.readAppearance(input, recipeId)
                        : ItemStack.EMPTY);
            }
            ItemStack result = VersionSupport.readAppearance(input, recipeId);
            fresh.add(new CeCraftingEntry(recipeId, shapeless, width, height, List.copyOf(ingredients), result));
        }
        entries.clear();
        entries.addAll(fresh);
    }

    public List<CeCraftingEntry> all() {
        return List.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }
}
