package ru.starmc.starpayment.cache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerCacheListener implements Listener {

    PlayerCacheStorage playerCacheStorage;

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        playerCacheStorage.loadPlayerCache(player.getName(), player.getUniqueId());

    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (playerCacheStorage.isPlayerCacheLoad(player.getUniqueId()))
            playerCacheStorage.unloadAndSavePlayerCache(player.getUniqueId());

    }

    public void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}
