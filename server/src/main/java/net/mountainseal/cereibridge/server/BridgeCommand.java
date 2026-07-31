package net.mountainseal.cereibridge.server;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.mountainseal.cereibridge.server.sync.SyncManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public final class BridgeCommand implements BasicCommand {
    private final CraftEngineReiBridgePlugin plugin;
    private final SyncManager syncManager;

    public BridgeCommand(CraftEngineReiBridgePlugin plugin, SyncManager syncManager) {
        this.plugin = plugin;
        this.syncManager = syncManager;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        if (args.length == 0) {
            sender.sendMessage("/cereibridge <reload|resync|info>");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                syncManager.rebuild();
                sender.sendMessage("CraftEngine REI sync cache rebuilt.");
            }
            case "resync" -> {
                if (sender instanceof Player player) {
                    plugin.pushAllTo(player);
                } else {
                    plugin.getServer().getOnlinePlayers().forEach(plugin::pushAllTo);
                }
                sender.sendMessage("CraftEngine REI data sent.");
            }
            case "info" -> sender.sendMessage(syncManager.describe());
            default -> sender.sendMessage("Unknown subcommand.");
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return args.length == 1 ? List.of("reload", "resync", "info") : List.of();
    }

    @Override
    public String permission() {
        return "craftengine-rei-bridge.admin";
    }
}
