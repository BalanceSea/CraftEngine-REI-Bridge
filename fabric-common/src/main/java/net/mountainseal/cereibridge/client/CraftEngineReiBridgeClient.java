package net.mountainseal.cereibridge.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mountainseal.cereibridge.client.cache.CeBrewingRegistry;
import net.mountainseal.cereibridge.client.cache.CeCookingRegistry;
import net.mountainseal.cereibridge.client.cache.CeCraftingRegistry;
import net.mountainseal.cereibridge.client.cache.CeItemRegistry;
import net.mountainseal.cereibridge.client.cache.CeSmithingRegistry;
import net.mountainseal.cereibridge.client.cache.CeStonecuttingRegistry;
import net.mountainseal.cereibridge.client.net.BridgeChannels;
import net.mountainseal.cereibridge.client.net.ChunkAssembler;
import net.mountainseal.cereibridge.client.net.HelloPayload;
import net.mountainseal.cereibridge.client.platform.VersionSupport;
import net.mountainseal.cereibridge.client.rei.CeReiPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class CraftEngineReiBridgeClient implements ClientModInitializer {
    public static final String MOD_ID = "craftengine_rei_bridge";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final CeItemRegistry ITEMS = new CeItemRegistry();
    private static final CeCraftingRegistry CRAFTING = new CeCraftingRegistry();
    private static final CeSmithingRegistry SMITHING = new CeSmithingRegistry();
    private static final CeBrewingRegistry BREWING = new CeBrewingRegistry();
    private static final CeCookingRegistry SMELTING = new CeCookingRegistry("smelting");
    private static final CeCookingRegistry BLASTING = new CeCookingRegistry("blasting");
    private static final CeCookingRegistry SMOKING = new CeCookingRegistry("smoking");
    private static final CeCookingRegistry CAMPFIRE = new CeCookingRegistry("campfire");
    private static final CeStonecuttingRegistry STONECUTTING = new CeStonecuttingRegistry();

    private static final int HELLO_MAX_ATTEMPTS = 200;
    private static boolean helloPending;
    private static int helloAttempts;

    public static CeItemRegistry items() {
        return ITEMS;
    }

    public static CeCraftingRegistry crafting() {
        return CRAFTING;
    }

    public static CeSmithingRegistry smithing() {
        return SMITHING;
    }

    public static CeBrewingRegistry brewing() {
        return BREWING;
    }

    public static CeCookingRegistry smelting() {
        return SMELTING;
    }

    public static CeCookingRegistry blasting() {
        return BLASTING;
    }

    public static CeCookingRegistry smoking() {
        return SMOKING;
    }

    public static CeCookingRegistry campfire() {
        return CAMPFIRE;
    }

    public static CeStonecuttingRegistry stonecutting() {
        return STONECUTTING;
    }

    @Override
    public void onInitializeClient() {
        registerPayloadTypes();

        ChunkAssembler itemAssembler = new ChunkAssembler();
        ChunkAssembler craftingAssembler = new ChunkAssembler();
        ChunkAssembler smithingAssembler = new ChunkAssembler();
        ChunkAssembler brewingAssembler = new ChunkAssembler();
        ChunkAssembler smeltingAssembler = new ChunkAssembler();
        ChunkAssembler blastingAssembler = new ChunkAssembler();
        ChunkAssembler smokingAssembler = new ChunkAssembler();
        ChunkAssembler campfireAssembler = new ChunkAssembler();
        ChunkAssembler stonecuttingAssembler = new ChunkAssembler();

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.ITEMS, (payload, context) ->
                itemAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        ITEMS.readFrom(input);
                        CeReiPlugin.onItemsUpdated();
                        LOGGER.info("Loaded {} CraftEngine items", ITEMS.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine item sync", exception);
                    }
                })));

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.CRAFTING, (payload, context) ->
                craftingAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        CRAFTING.readFrom(input);
                        CeReiPlugin.onCraftingUpdated();
                        LOGGER.info("Loaded {} CraftEngine crafting recipes", CRAFTING.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine crafting sync", exception);
                    }
                })));

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.SMITHING, (payload, context) ->
                smithingAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        SMITHING.readFrom(input);
                        CeReiPlugin.onSmithingUpdated();
                        LOGGER.info("Loaded {} CraftEngine smithing recipes", SMITHING.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine smithing sync", exception);
                    }
                })));

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.BREWING, (payload, context) ->
                brewingAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        BREWING.readFrom(input);
                        CeReiPlugin.onBrewingUpdated();
                        LOGGER.info("Loaded {} CraftEngine brewing recipes", BREWING.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine brewing sync", exception);
                    }
                })));

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.SMELTING, (payload, context) ->
                smeltingAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        SMELTING.readFrom(input);
                        CeReiPlugin.onSmeltingUpdated();
                        LOGGER.info("Loaded {} CraftEngine smelting recipes", SMELTING.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine smelting sync", exception);
                    }
                })));

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.BLASTING, (payload, context) ->
                blastingAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        BLASTING.readFrom(input);
                        CeReiPlugin.onBlastingUpdated();
                        LOGGER.info("Loaded {} CraftEngine blasting recipes", BLASTING.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine blasting sync", exception);
                    }
                })));

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.SMOKING, (payload, context) ->
                smokingAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        SMOKING.readFrom(input);
                        CeReiPlugin.onSmokingUpdated();
                        LOGGER.info("Loaded {} CraftEngine smoking recipes", SMOKING.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine smoking sync", exception);
                    }
                })));

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.CAMPFIRE, (payload, context) ->
                campfireAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        CAMPFIRE.readFrom(input);
                        CeReiPlugin.onCampfireUpdated();
                        LOGGER.info("Loaded {} CraftEngine campfire recipes", CAMPFIRE.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine campfire sync", exception);
                    }
                })));

        ClientPlayNetworking.registerGlobalReceiver(BridgeChannels.STONECUTTING, (payload, context) ->
                stonecuttingAssembler.accept(payload).ifPresent(bytes -> context.client().execute(() -> {
                    try (DataInputStream input = stream(bytes)) {
                        STONECUTTING.readFrom(input);
                        CeReiPlugin.onStonecuttingUpdated();
                        LOGGER.info("Loaded {} CraftEngine stonecutting recipes", STONECUTTING.all().size());
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse the CraftEngine stonecutting sync", exception);
                    }
                })));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            helloPending = true;
            helloAttempts = 0;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            helloPending = false;
            itemAssembler.reset();
            craftingAssembler.reset();
            smithingAssembler.reset();
            brewingAssembler.reset();
            smeltingAssembler.reset();
            blastingAssembler.reset();
            smokingAssembler.reset();
            campfireAssembler.reset();
            stonecuttingAssembler.reset();
            clearSyncedData();
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!helloPending) {
                return;
            }
            if (ClientPlayNetworking.canSend(HelloPayload.TYPE)) {
                ClientPlayNetworking.send(new HelloPayload());
                helloPending = false;
            } else if (++helloAttempts > HELLO_MAX_ATTEMPTS) {
                LOGGER.warn("The server did not advertise the CraftEngine REI bridge channel within 10 seconds");
                helloPending = false;
            }
        });
    }

    private static void registerPayloadTypes() {
        VersionSupport.registerPayloadTypes();
    }

    private static DataInputStream stream(byte[] bytes) {
        return new DataInputStream(new ByteArrayInputStream(bytes));
    }

    private static void clearSyncedData() {
        ITEMS.clear();
        CRAFTING.clear();
        SMITHING.clear();
        BREWING.clear();
        SMELTING.clear();
        BLASTING.clear();
        SMOKING.clear();
        CAMPFIRE.clear();
        STONECUTTING.clear();
        CeReiPlugin.onItemsUpdated();
        CeReiPlugin.onCraftingUpdated();
        CeReiPlugin.onSmithingUpdated();
        CeReiPlugin.onBrewingUpdated();
        CeReiPlugin.onSmeltingUpdated();
        CeReiPlugin.onBlastingUpdated();
        CeReiPlugin.onSmokingUpdated();
        CeReiPlugin.onCampfireUpdated();
        CeReiPlugin.onStonecuttingUpdated();
    }
}
