package net.mountainseal.cereibridge.client.platform;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.mountainseal.cereibridge.client.net.BridgeChannels;
import net.mountainseal.cereibridge.client.net.ChunkPayload;
import net.mountainseal.cereibridge.client.net.HelloPayload;

public final class VersionSupport {
    private VersionSupport() {
    }

    public static ItemStack readAppearance(DataInputStream input, String craftEngineId) throws IOException {
        Identifier baseId = Identifier.parse(input.readUTF());
        Item item = BuiltInRegistries.ITEM.getValue(baseId);
        ItemStack stack = new ItemStack(item);
        if (input.readBoolean()) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA,
                    new CustomModelData(List.of((float) input.readInt()), List.of(), List.of(), List.of()));
        }
        if (input.readBoolean()) {
            stack.set(DataComponents.ITEM_MODEL, Identifier.parse(input.readUTF()));
        }
        if (input.readBoolean()) {
            String json = input.readUTF();
            JsonElement element = JsonParser.parseString(json);
            Component name = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, element)
                    .resultOrPartial(error -> { })
                    .orElseGet(() -> Component.literal(json));
            stack.set(DataComponents.CUSTOM_NAME, name);
        }
        String identityId = input.readUTF();
        if (!identityId.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("craftengine:id", identityId);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return stack;
    }

    public static ItemStack itemStack(String id) {
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    public static void registerPayloadTypes() {
        PayloadTypeRegistry.playS2C().register(BridgeChannels.ITEMS, ChunkPayload.codecFor(BridgeChannels.ITEMS));
        PayloadTypeRegistry.playS2C().register(BridgeChannels.CRAFTING, ChunkPayload.codecFor(BridgeChannels.CRAFTING));
        PayloadTypeRegistry.playS2C().register(BridgeChannels.SMITHING, ChunkPayload.codecFor(BridgeChannels.SMITHING));
        PayloadTypeRegistry.playS2C().register(BridgeChannels.BREWING, ChunkPayload.codecFor(BridgeChannels.BREWING));
        PayloadTypeRegistry.playS2C().register(BridgeChannels.SMELTING, ChunkPayload.codecFor(BridgeChannels.SMELTING));
        PayloadTypeRegistry.playS2C().register(BridgeChannels.BLASTING, ChunkPayload.codecFor(BridgeChannels.BLASTING));
        PayloadTypeRegistry.playS2C().register(BridgeChannels.SMOKING, ChunkPayload.codecFor(BridgeChannels.SMOKING));
        PayloadTypeRegistry.playS2C().register(BridgeChannels.CAMPFIRE, ChunkPayload.codecFor(BridgeChannels.CAMPFIRE));
        PayloadTypeRegistry.playS2C().register(BridgeChannels.STONECUTTING, ChunkPayload.codecFor(BridgeChannels.STONECUTTING));
        PayloadTypeRegistry.playC2S().register(HelloPayload.TYPE, HelloPayload.CODEC);
    }
}
