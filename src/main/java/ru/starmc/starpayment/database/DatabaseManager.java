package ru.starmc.starpayment.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.starmc.starpayment.database.model.PlayerModel;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final Dao<PlayerModel, String> playersDao;

    public DatabaseManager(ConnectionGenerator connectionGenerator) throws SQLException {

        ConnectionSource connection;

        connection = connectionGenerator.getConnectionSource();

        this.playersDao = DaoManager.createDao(connection, PlayerModel.class);
        TableUtils.createTableIfNotExists(connection, PlayerModel.class);

    }

    /*
     * PlayerModel
     */

    public CompletableFuture<Boolean> clearPlayerTable() {

        return CompletableFuture.supplyAsync(() -> {
            try {
                TableUtils.clearTable(playersDao.getConnectionSource(), PlayerModel.class);
                return true;
            } catch (SQLException exception) {
                exception.printStackTrace();
                return false;
            }
        });

    }

    public CompletableFuture<Boolean> isPlayerExists(String playerName) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return playersDao.idExists(playerName);
            } catch (SQLException exception) {
                exception.printStackTrace();
                return false;
            }
        });

    }

    public CompletableFuture<PlayerModel> getPlayerModel(String playerName) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return playersDao.queryForId(playerName);
            } catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        });

    }

    public CompletableFuture<Boolean> setPlayerModel(PlayerModel playerModel) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                playersDao.createOrUpdate(playerModel);
                return true;
            } catch (SQLException exception) {
                exception.printStackTrace();
                return false;
            }
        });

    }

    public CompletableFuture<Boolean> createOrUpdatePlayerModel(PlayerModel playerModel) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                playersDao.createOrUpdate(playerModel);
                return true;
            } catch (SQLException exception) {
                exception.printStackTrace();
                return false;
            }
        });

    }

    public CompletableFuture<PlayerModel> createPlayerModelIfNotExists(PlayerModel playerModel) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return playersDao.createIfNotExists(playerModel);
            } catch (SQLException exception) {
                return null;
            }
        });

    }

}
