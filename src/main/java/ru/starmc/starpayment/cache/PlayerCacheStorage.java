package ru.starmc.starpayment.cache;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.starmc.starpayment.bonus.BonusContainer;
import ru.starmc.starpayment.database.DatabaseManager;
import ru.starmc.starpayment.database.model.PlayerModel;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerCacheStorage {

    DatabaseManager databaseManager;
    BonusContainer bonusContainer;

    ConcurrentHashMap<UUID, PlayerCache> playerCacheMap = new ConcurrentHashMap<>();

    public PlayerCacheStorage(DatabaseManager databaseManager, BonusContainer bonusContainer) {
        this.databaseManager = databaseManager;
        this.bonusContainer = bonusContainer;
    }

    public void loadPlayerCache(String playerName, UUID playerUUID) {

        databaseManager.createPlayerModelIfNotExists(new PlayerModel(playerName, playerUUID)).thenAccept(model -> {

            bonusContainer.getPlayerBonus(playerUUID).thenAccept(ownBonus -> {

                if (model.getReferName() == null) {
                    playerCacheMap.put(
                            playerUUID,
                            new PlayerCache(model, ownBonus, bonusContainer.getDefaultBonus()));
                }

                databaseManager.getPlayerModel(model.getReferName()).thenAccept(referModel -> {

                    bonusContainer.getPlayerBonus(referModel.getPlayerUUID()).thenAccept(referBonus -> {
                        playerCacheMap.put(
                                playerUUID,
                                new PlayerCache(model, ownBonus, referBonus));
                    });

                });

            });

        });

    }

    public void unloadAndSavePlayerCache(UUID playerUUID) {
        databaseManager.setPlayerModel(playerCacheMap.get(playerUUID).getPlayerModel())
                .thenAccept(state -> playerCacheMap.remove(playerUUID));
    }

    public PlayerCache getPlayerCache(UUID playerUUID) {
        return this.playerCacheMap.get(playerUUID);
    }

    public boolean isPlayerCacheLoad(UUID playerUUID) {
        return playerCacheMap.containsKey(playerUUID);
    }

}
