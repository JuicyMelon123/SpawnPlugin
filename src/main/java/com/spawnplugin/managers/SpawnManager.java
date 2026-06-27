package com.spawnplugin.managers;

import com.spawnplugin.SpawnPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Spawn konumunu ve ısınma zamanlayıcılarını yöneten merkezi sınıf.
 *
 * ──────────────────────────────────────────────
 *  DEĞİŞTİRİLEBİLİR AYARLAR (config.yml'den okunur)
 * ──────────────────────────────────────────────
 *  warmup-seconds  → /spawn komutundan sonra bekleme süresi
 *  messages.*      → Tüm sohbet mesajları
 */
public class SpawnManager {

    private final SpawnPlugin plugin;
    private final Messages messages;

    /** /spawn ısınma süresi (saniye) - config.yml'den okunur */
    private final int WARMUP_SECONDS;

    /** Aktif ısınma zamanlayıcıları — UUID → BukkitTask */
    private final Map<UUID, BukkitTask> warmupTasks = new HashMap<>();

    /** Işınlanma başlangıç konumları (hareket tespiti için) */
    private final Map<UUID, Location> warmupLocations = new HashMap<>();

    public SpawnManager(SpawnPlugin plugin) {
        this.plugin = plugin;
        this.messages = new Messages(plugin.getConfig());
        this.WARMUP_SECONDS = plugin.getConfig().getInt("warmup-seconds", 5);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  SPAWN KONUMU KAYDET / OKU
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Mevcut konumu spawn noktası olarak kaydeder ve config.yml'e yazar.
     */
    public void setSpawn(Location loc) {
        plugin.getConfig().set("spawn.world", loc.getWorld().getName());
        plugin.getConfig().set("spawn.x",     loc.getX());
        plugin.getConfig().set("spawn.y",     loc.getY());
        plugin.getConfig().set("spawn.z",     loc.getZ());
        plugin.getConfig().set("spawn.yaw",   loc.getYaw());
        plugin.getConfig().set("spawn.pitch", loc.getPitch());
        plugin.saveConfig();
    }

    /**
     * Kaydedilmiş spawn konumunu döner. Ayarlanmamışsa null döner.
     */
    public Location getSpawn() {
        String worldName = plugin.getConfig().getString("spawn.world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x     = plugin.getConfig().getDouble("spawn.x");
        double y     = plugin.getConfig().getDouble("spawn.y");
        double z     = plugin.getConfig().getDouble("spawn.z");
        float  yaw   = (float) plugin.getConfig().getDouble("spawn.yaw");
        float  pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Spawn noktasının ayarlanıp ayarlanmadığını kontrol eder.
     */
    public boolean isSpawnSet() {
        return plugin.getConfig().getString("spawn.world") != null;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ISINMA (WARMUP)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * /spawn komutunda ısınma zamanlayıcısını başlatır.
     */
    public void startWarmup(Player player) {
        UUID uuid = player.getUniqueId();

        // Zaten ısınma varsa iptal et (tekrar /spawn yazmasın diye)
        if (warmupTasks.containsKey(uuid)) {
            player.sendMessage(messages.get("warmup-already-active"));
            return;
        }

        warmupLocations.put(uuid, player.getLocation().clone());
        player.sendMessage(messages.get("warmup-started",
                Map.of("seconds", String.valueOf(WARMUP_SECONDS))));

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            warmupTasks.remove(uuid);
            warmupLocations.remove(uuid);

            if (!player.isOnline()) return;

            Location spawn = getSpawn();
            if (spawn == null) {
                player.sendMessage(messages.get("warmup-spawn-missing"));
                return;
            }

            player.teleport(spawn);
            player.sendMessage(messages.get("teleport-success"));

        }, WARMUP_SECONDS * 20L);

        warmupTasks.put(uuid, task);
    }

    /**
     * Bir oyuncunun ısınma zamanlayıcısını iptal eder (hareket algılandığında).
     */
    public void cancelWarmup(UUID uuid) {
        BukkitTask task = warmupTasks.remove(uuid);
        if (task != null) {
            task.cancel();
            warmupLocations.remove(uuid);
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                p.sendMessage(messages.get("warmup-cancelled-move"));
            }
        }
    }

    public boolean hasWarmup(UUID uuid) {
        return warmupTasks.containsKey(uuid);
    }

    public Location getWarmupLocation(UUID uuid) {
        return warmupLocations.get(uuid);
    }

    public Messages getMessages() {
        return messages;
    }
}
