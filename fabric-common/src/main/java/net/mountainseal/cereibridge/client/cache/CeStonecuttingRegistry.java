package net.mountainseal.cereibridge.client.cache;

import net.mountainseal.cereibridge.client.platform.VersionSupport;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CeStonecuttingRegistry {
    private final List<CeStonecuttingEntry> entries = new ArrayList<>();

    public void readFrom(DataInputStream input) throws IOException {
        List<CeStonecuttingEntry> fresh = new ArrayList<>();
        int count = input.readInt();
        if (count < 0 || count > 100_000) {
            throw new IOException("Invalid stonecutting recipe count: " + count);
        }
        for (int index = 0; index < count; index++) {
            String recipeId = input.readUTF();
            fresh.add(new CeStonecuttingEntry(
                    recipeId,
                    VersionSupport.readAppearance(input, recipeId),
                    VersionSupport.readAppearance(input, recipeId)
            ));
        }
        entries.clear();
        entries.addAll(fresh);
    }

    public List<CeStonecuttingEntry> all() {
        return List.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }
}
