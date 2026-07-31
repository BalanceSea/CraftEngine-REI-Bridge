package net.mountainseal.cereibridge.client.cache;

import net.mountainseal.cereibridge.client.platform.VersionSupport;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CeCookingRegistry {
    private final String recipeType;
    private final List<CeCookingEntry> entries = new ArrayList<>();

    public CeCookingRegistry(String recipeType) {
        this.recipeType = recipeType;
    }

    public void readFrom(DataInputStream input) throws IOException {
        List<CeCookingEntry> fresh = new ArrayList<>();
        int count = input.readInt();
        if (count < 0 || count > 100_000) {
            throw new IOException("Invalid " + recipeType + " recipe count: " + count);
        }
        for (int index = 0; index < count; index++) {
            String recipeId = input.readUTF();
            fresh.add(new CeCookingEntry(
                    recipeId,
                    VersionSupport.readAppearance(input, recipeId),
                    VersionSupport.readAppearance(input, recipeId),
                    input.readFloat(),
                    input.readInt()
            ));
        }
        entries.clear();
        entries.addAll(fresh);
    }

    public List<CeCookingEntry> all() {
        return List.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }
}
