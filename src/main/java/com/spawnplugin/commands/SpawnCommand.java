package com.spawnplugin.commands;

import com.spawnplugin.SpawnPlugin;
import com.spawnplugin.managers.SpawnManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /spawn komutu — Tüm oyuncular kullanabilir.
 *
 * Oyuncuyu ısınma süresinin ardından spawn noktasına ışınlar.
 * Bekleme süresi içinde hareket ederse ışınlanma iptal edilir.
 */
public class SpawnCommand implements CommandExecutor {

    private final SpawnPlugin plugin;
    private final SpawnManager spawnManager;

    public SpawnCommand(SpawnPlugin plugin, SpawnManager spawnManager) {
        this.plugin       = plugin;
        this.spawnManager = spawnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(spawnManager.getMessages().get("player-only-command"));
            return true;
        }

        if (!player.hasPermission("spawnplugin.spawn")) {
            player.sendMessage(spawnManager.getMessages().get("spawn-no-permission"));
            return true;
        }

        // Spawn ayarlanmış mı?
        if (!spawnManager.isSpawnSet()) {
            player.sendMessage(spawnManager.getMessages().get("spawn-not-set"));
            return true;
        }

        // Işınma ısınmasını başlat
        spawnManager.startWarmup(player);
        return true;
    }
}
