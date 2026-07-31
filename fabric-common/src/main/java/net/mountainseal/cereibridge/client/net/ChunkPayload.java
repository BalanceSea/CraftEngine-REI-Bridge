package net.mountainseal.cereibridge.client.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ChunkPayload(
        CustomPacketPayload.Type<ChunkPayload> payloadType,
        int total,
        int index,
        byte[] data
) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return payloadType;
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ChunkPayload> codecFor(Type<ChunkPayload> type) {
        return StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ChunkPayload::total,
                ByteBufCodecs.VAR_INT, ChunkPayload::index,
                ByteBufCodecs.BYTE_ARRAY, ChunkPayload::data,
                (total, index, data) -> new ChunkPayload(type, total, index, data)
        );
    }
}
