package com.spawnplugin.managers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

/**
 * Loads all player-facing chat messages from config.yml ("messages" section)
 * and formats them with color codes + placeholder substitution.
 *
 * Yeni mesaj eklemek için: config.yml'de "messages" altına bir satır ekle,
 * sonra get("anahtar") veya get("anahtar", Map.of("yerTutucu", deger)) ile çağır.
 */
public class Messages {

    private final FileConfiguration config;

    public Messages(FileConfiguration config) {
        this.config = config;
    }

    /**
     * Returns the raw message for the given key with color codes translated.
     * If the key is missing from config.yml, returns the key itself wrapped
     * in red so missing translations are obvious instead of crashing.
     */
    public String get(String key) {
        String raw = config.getString("messages." + key);
        if (raw == null) {
            return ChatColor.RED + "Missing message: " + key;
        }
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    /**
     * Returns the message for the given key with %placeholder% values replaced.
     * Example: get("warmup-started", Map.of("seconds", "5"))
     */
    public String get(String key, Map<String, String> placeholders) {
        String message = get(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }
}
