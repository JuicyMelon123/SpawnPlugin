package com.spawnplugin;

import com.spawnplugin.commands.SetSpawnCommand;
import com.spawnplugin.commands.SpawnCommand;
import com.spawnplugin.listeners.JoinListener;
import com.spawnplugin.listeners.MoveListener;
import com.spawnplugin.managers.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SpawnPlugin - Ana sınıf
 *
 * Ayarlanabilir değerler config.yml dosyasında bulunmaktadır:
 *   - warmup-seconds  → /spawn komutundan sonra bekleme süresi (saniye)
 *   - spawn.*         → Spawn konumu (otomatik kaydedilir, elle düzenleme önerilmez)
 */
public class SpawnPlugin extends JavaPlugin {

    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        spawnManager = new SpawnManager(this);

        getCommand("setspawn").setExecutor(new SetSpawnCommand(this, spawnManager));
        getCommand("spawn").setExecutor(new SpawnCommand(this, spawnManager));

        getServer().getPluginManager().registerEvents(new MoveListener(spawnManager), this);
        getServer().getPluginManager().registerEvents(new JoinListener(spawnManager), this);

        getLogger().info("SpawnPlugin etkinleştirildi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpawnPlugin devre dışı bırakıldı!");
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }
}
