package net.mountainseal.cereibridge.server;

import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent;
import net.mountainseal.cereibridge.server.net.BridgeChannels;
import net.mountainseal.cereibridge.server.sync.SyncManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftEngineReiBridgePlugin extends JavaPlugin implements Listener, PluginMessageListener {
    private SyncManager syncManager;

    @Override
    public void onEnable() {
        syncManager = new SyncManager(this);
        registerChannels();
        getServer().getPluginManager().registerEvents(this, this);

        BridgeCommand command = new BridgeCommand(this, syncManager);
        registerCommand("cereibridge", "Manage CraftEngine REI synchronization", command);

        syncManager.rebuild();
        getLogger().info("CraftEngine REI Bridge enabled. Author: MoutainSeaL, QQ: 3643203568, group: 342097496");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    private void registerChannels() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.ITEMS);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.CRAFTING);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.SMITHING);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.BREWING);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.SMELTING);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.BLASTING);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.SMOKING);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.CAMPFIRE);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BridgeChannels.STONECUTTING);
        getServer().getMessenger().registerIncomingPluginChannel(this, BridgeChannels.HELLO, this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (BridgeChannels.HELLO.equals(channel)) {
            pushAllTo(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getServer().getScheduler().runTaskLater(this, () -> {
            if (player.isOnline()) {
                pushAllTo(player);
            }
        }, 40L);
    }

    @EventHandler
    public void onCraftEngineReload(CraftEngineReloadEvent event) {
        syncManager.rebuild();
        getServer().getOnlinePlayers().forEach(this::pushAllTo);
    }

    public void pushAllTo(Player player) {
        BridgeChannels.send(this, player, BridgeChannels.ITEMS, syncManager.itemsPayload());
        BridgeChannels.send(this, player, BridgeChannels.CRAFTING, syncManager.craftingPayload());
        BridgeChannels.send(this, player, BridgeChannels.SMITHING, syncManager.smithingPayload());
        BridgeChannels.send(this, player, BridgeChannels.BREWING, syncManager.brewingPayload());
        BridgeChannels.send(this, player, BridgeChannels.SMELTING, syncManager.smeltingPayload());
        BridgeChannels.send(this, player, BridgeChannels.BLASTING, syncManager.blastingPayload());
        BridgeChannels.send(this, player, BridgeChannels.SMOKING, syncManager.smokingPayload());
        BridgeChannels.send(this, player, BridgeChannels.CAMPFIRE, syncManager.campfirePayload());
        BridgeChannels.send(this, player, BridgeChannels.STONECUTTING, syncManager.stonecuttingPayload());
    }
}
