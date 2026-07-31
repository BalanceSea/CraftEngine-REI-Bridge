package net.mountainseal.cereibridge.server.net;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BridgeChannels {
    public static final String ITEMS = "ce_rei_bridge:items";
    public static final String CRAFTING = "ce_rei_bridge:crafting";
    public static final String SMITHING = "ce_rei_bridge:smithing";
    public static final String BREWING = "ce_rei_bridge:brewing";
    public static final String SMELTING = "ce_rei_bridge:smelting";
    public static final String BLASTING = "ce_rei_bridge:blasting";
    public static final String SMOKING = "ce_rei_bridge:smoking";
    public static final String CAMPFIRE = "ce_rei_bridge:campfire";
    public static final String STONECUTTING = "ce_rei_bridge:stonecutting";
    public static final String HELLO = "ce_rei_bridge:hello";

    private static final int MAX_CHUNK_BYTES = 30_000;

    private BridgeChannels() {
    }

    public static void send(Plugin plugin, Player player, String channel, byte[] fullPayload) {
        List<byte[]> chunks = new ArrayList<>();
        for (int offset = 0; offset < fullPayload.length; offset += MAX_CHUNK_BYTES) {
            int end = Math.min(offset + MAX_CHUNK_BYTES, fullPayload.length);
            chunks.add(Arrays.copyOfRange(fullPayload, offset, end));
        }
        if (chunks.isEmpty()) {
            chunks.add(new byte[0]);
        }
        for (int index = 0; index < chunks.size(); index++) {
            player.sendPluginMessage(plugin, channel, frame(chunks.size(), index, chunks.get(index)));
        }
    }

    private static byte[] frame(int total, int index, byte[] body) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream(body.length + 16);
            DataOutputStream output = new DataOutputStream(bytes);
            writeVarInt(output, total);
            writeVarInt(output, index);
            writeVarInt(output, body.length);
            output.write(body);
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to frame a bridge payload", exception);
        }
    }

    private static void writeVarInt(DataOutputStream output, int value) throws IOException {
        while ((value & ~0x7F) != 0) {
            output.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        output.writeByte(value);
    }
}
