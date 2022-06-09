package ru.starmc.starpayment.menu.gui;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MenuClickDelay {

    int delay;
    JavaPlugin javaPlugin;

    Set<UUID> playersInDelay = new HashSet<>();

    public MenuClickDelay(int delay, JavaPlugin javaPlugin) {
        this.delay = delay;
        this.javaPlugin = javaPlugin;
    }

    public void addPlayer(UUID playerUUID) {

        if (playersInDelay.contains(playerUUID)) return;
        playersInDelay.add(playerUUID);

        javaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(
                javaPlugin,
                ()-> playersInDelay.remove(playerUUID),
                delay);

    }

    public boolean isPlayerInDelay(UUID playerUUID) {
        return playersInDelay.contains(playerUUID);
    }

}
