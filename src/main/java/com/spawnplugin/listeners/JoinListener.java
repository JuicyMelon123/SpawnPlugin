package com.spawnplugin.listeners;

import com.spawnplugin.managers.SpawnManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Yeni oyuncu katılım dinleyicisi.
 *
 * Sunucuya ilk kez ya da tekrar katılan her oyuncuyu
 * ayarlanmış spawn noktasına ışınlar.
 * Bu sayede OP'nin her yeni oyuncu için tekrar komut çalıştırması gerekmez.
 */
public class JoinListener implements Listener {

    private final SpawnManager spawnManager;

    public JoinListener(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!spawnManager.isSpawnSet()) return;

        Location spawn = spawnManager.getSpawn();
        if (spawn == null) return;

        // Sadece ilk kez giren oyuncuyu spawn'a ışınla
        // hasPlayedBefore() → true ise daha önce girmiş, atla
        if (event.getPlayer().hasPlayedBefore()) return;

        event.getPlayer().teleport(spawn);
    }
}
