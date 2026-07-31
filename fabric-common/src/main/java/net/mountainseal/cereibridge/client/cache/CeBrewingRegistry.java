package net.mountainseal.cereibridge.client.cache;

import net.mountainseal.cereibridge.client.platform.VersionSupport;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CeBrewingRegistry {
    private final List<CeBrewingEntry> entries = new ArrayList<>();

    public void readFrom(DataInputStream input) throws IOException {
        List<CeBrewingEntry> fresh = new ArrayList<>();
        int count = input.readInt();
        if (count < 0 || count > 100_000) {
            throw new IOException("Invalid brewing recipe count: " + count);
        }
        for (int index = 0; index < count; index++) {
            String recipeId = input.readUTF();
            String ingredientId = input.readUTF();
            fresh.add(new CeBrewingEntry(recipeId, ingredientId, VersionSupport.readAppearance(input, recipeId)));
        }
        entries.clear();
        entries.addAll(fresh);
    }

    public List<CeBrewingEntry> all() {
        return List.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }
}
