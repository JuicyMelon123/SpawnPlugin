package com.spawnplugin.listeners;

import com.spawnplugin.managers.SpawnManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Oyuncu hareketi dinleyicisi.
 *
 * /spawn ısınması sırasında oyuncu hareket ederse ışınlanma iptal edilir.
 * Oyuncu sunucudan ayrılırsa da ısınma iptal edilir.
 */
public class MoveListener implements Listener {

    private final SpawnManager spawnManager;

    public MoveListener(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!spawnManager.hasWarmup(player.getUniqueId())) return;

        Location from = event.getFrom();
        Location to   = event.getTo();

        if (to == null) return;

        // Sadece gerçek pozisyon değişikliğini kontrol et (kafa dönmesi sayılmaz)
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        spawnManager.cancelWarmup(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        spawnManager.cancelWarmup(event.getPlayer().getUniqueId());
    }
}
