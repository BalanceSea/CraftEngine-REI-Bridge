package net.mountainseal.cereibridge.client.cache;

import net.mountainseal.cereibridge.client.platform.VersionSupport;
import net.minecraft.world.item.ItemStack;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CeSmithingRegistry {
    private final List<CeSmithingEntry> entries = new ArrayList<>();

    public void readFrom(DataInputStream input) throws IOException {
        List<CeSmithingEntry> fresh = new ArrayList<>();
        int count = input.readInt();
        if (count < 0 || count > 100_000) {
            throw new IOException("Invalid smithing recipe count: " + count);
        }
        for (int index = 0; index < count; index++) {
            String recipeId = input.readUTF();
            ItemStack template = readOptional(input, recipeId);
            ItemStack base = readOptional(input, recipeId);
            ItemStack addition = readOptional(input, recipeId);
            ItemStack result = VersionSupport.readAppearance(input, recipeId);
            fresh.add(new CeSmithingEntry(recipeId, template, base, addition, result));
        }
        entries.clear();
        entries.addAll(fresh);
    }

    private static ItemStack readOptional(DataInputStream input, String recipeId) throws IOException {
        return input.readBoolean() ? VersionSupport.readAppearance(input, recipeId) : ItemStack.EMPTY;
    }

    public List<CeSmithingEntry> all() {
        return List.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }
}
