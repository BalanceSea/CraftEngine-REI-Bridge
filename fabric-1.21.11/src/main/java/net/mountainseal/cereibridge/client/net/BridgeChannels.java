package net.mountainseal.cereibridge.client.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class BridgeChannels {
    public static final CustomPacketPayload.Type<ChunkPayload> ITEMS = type("items");
    public static final CustomPacketPayload.Type<ChunkPayload> CRAFTING = type("crafting");
    public static final CustomPacketPayload.Type<ChunkPayload> SMITHING = type("smithing");
    public static final CustomPacketPayload.Type<ChunkPayload> BREWING = type("brewing");

    private BridgeChannels() {
    }

    private static CustomPacketPayload.Type<ChunkPayload> type(String path) {
        return new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ce_rei_bridge", path));
    }
}
