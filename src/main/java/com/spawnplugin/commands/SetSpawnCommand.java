package com.spawnplugin.commands;

import com.spawnplugin.SpawnPlugin;
import com.spawnplugin.managers.SpawnManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * /setspawn komutu — Sadece OP oyuncular kullanabilir.
 *
 * Ayak bastığın konumu sunucunun spawn noktası olarak kaydeder.
 * Tüm oyuncular (mevcut ve yeni katılacaklar) bu spawn noktasını kullanır.
 */
public class SetSpawnCommand implements CommandExecutor {

    private final SpawnPlugin plugin;
    private final SpawnManager spawnManager;

    public SetSpawnCommand(SpawnPlugin plugin, SpawnManager spawnManager) {
        this.plugin       = plugin;
        this.spawnManager = spawnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Konsol kontrolü
        if (!(sender instanceof Player player)) {
            sender.sendMessage(spawnManager.getMessages().get("player-only-command"));
            return true;
        }

        // İzin kontrolü (plugin.yml'de default: op olarak ayarlandı)
        if (!player.hasPermission("spawnplugin.setspawn")) {
            player.sendMessage(spawnManager.getMessages().get("setspawn-no-permission"));
            return true;
        }

        // Spawn konumunu kaydet
        spawnManager.setSpawn(player.getLocation());

        player.sendMessage(spawnManager.getMessages().get("setspawn-success"));
        player.sendMessage(spawnManager.getMessages().get("setspawn-location-info", Map.of(
                "x", String.valueOf((int) player.getLocation().getX()),
                "y", String.valueOf((int) player.getLocation().getY()),
                "z", String.valueOf((int) player.getLocation().getZ()),
                "world", player.getWorld().getName()
        )));

        return true;
    }
}
