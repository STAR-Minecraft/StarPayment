package ru.starmc.starpayment.bonus;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.Configuration;
import ru.starmc.starpayment.hook.LuckPermsHook;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BonusContainer {

    private static final String DEFAULT_GROUP_KEY = "default";
    private static final Bonus DEFAULT_BONUS = new Bonus(1, 1);

    HashMap<String, Bonus> groupBonuses = new HashMap<>();
    LuckPermsHook luckPermsHook;

    public BonusContainer(Configuration config, LuckPermsHook luckPermsHook) {

        this.luckPermsHook = luckPermsHook;

        groupBonuses.put(DEFAULT_GROUP_KEY, DEFAULT_BONUS);

        config.getConfigurationSection("groupBonuses").getKeys(false).forEach(groupName -> {
            groupBonuses.put(
                    groupName,
                    new Bonus(config.getInt("groupBonuses." + groupName + ".own"),
                              config.getInt("groupBonuses." + groupName + ".referral")));
        });

    }

    public CompletableFuture<Bonus> getPlayerBonus(UUID playerUUID) {

        return CompletableFuture.supplyAsync(() -> {

            try {
                String groupName = luckPermsHook.getPrimaryGroup(playerUUID).get();
                if (groupBonuses.containsKey(groupName)) return groupBonuses.get(groupName);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            return DEFAULT_BONUS;

        });

    }

    public Bonus getDefaultBonus() {
        return DEFAULT_BONUS;
    }

}
