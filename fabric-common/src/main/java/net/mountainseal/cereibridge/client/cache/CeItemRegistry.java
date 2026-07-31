package net.mountainseal.cereibridge.client.cache;

import net.mountainseal.cereibridge.client.platform.VersionSupport;
import net.minecraft.world.item.ItemStack;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CeItemRegistry {
    private final Map<String, CeItem> entries = new LinkedHashMap<>();

    public void readFrom(DataInputStream input) throws IOException {
        Map<String, CeItem> fresh = new LinkedHashMap<>();
        int count = input.readInt();
        if (count < 0 || count > 1_000_000) {
            throw new IOException("Invalid item count: " + count);
        }
        for (int index = 0; index < count; index++) {
            String id = input.readUTF();
            fresh.put(id, new CeItem(id, VersionSupport.readAppearance(input, id)));
        }
        entries.clear();
        entries.putAll(fresh);
    }

    public Optional<CeItem> byId(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    public Collection<CeItem> all() {
        return List.copyOf(entries.values());
    }

    public void clear() {
        entries.clear();
    }
}
