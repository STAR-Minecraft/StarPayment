package ru.starmc.starpayment.hook;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsHook {

    private LuckPerms api;
    @Getter private boolean initialized = false;

    public LuckPermsHook(Server server) {

        RegisteredServiceProvider<LuckPerms> provider = server.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.api = provider.getProvider();
            initialized = true;
        }

    }

    public CompletableFuture<String> getPrimaryGroup(UUID playerUUID) {
        return api.getUserManager().loadUser(playerUUID).thenApplyAsync(User::getPrimaryGroup);
    }

}
