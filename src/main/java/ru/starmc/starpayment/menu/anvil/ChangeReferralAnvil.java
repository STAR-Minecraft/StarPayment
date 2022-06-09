package ru.starmc.starpayment.menu.anvil;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.starmc.starpayment.cache.PlayerCache;
import ru.starmc.starpayment.cache.PlayerCacheStorage;
import ru.starmc.starpayment.common.Colorizer;
import ru.starmc.starpayment.database.DatabaseManager;
import ru.starmc.starpayment.database.model.PlayerModel;
import ru.starmc.starpayment.message.MessageSender;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangeReferralAnvil implements AnvilMenu {

    private static final String CONFIG_PATH = "anvil.changeReferral";

    DatabaseManager databaseManager;
    PlayerCacheStorage playerCacheStorage;
    JavaPlugin pluginInstance;

    String title;
    String text;

    public ChangeReferralAnvil(Configuration config, DatabaseManager databaseManager, PlayerCacheStorage playerCacheStorage, JavaPlugin pluginInstance) {

        this.databaseManager = databaseManager;
        this.playerCacheStorage = playerCacheStorage;
        this.pluginInstance = pluginInstance;

        title = Colorizer.colorizeString(config.getString(CONFIG_PATH + ".title"));
        text = config.getString(CONFIG_PATH + ".text");

    }

    @Override
    public void open(Player player) {

        new AnvilGUI.Builder()
                .text(this.text)
                .title(this.title)
                .plugin(this.pluginInstance)
                .onComplete((user, text) -> {
                    MessageSender.sendMessage(user, "checkReferralCode");
                    this.acceptCodeIfCorrect(user, text);
                    return AnvilGUI.Response.close();
                })
                .open(player);

    }

    private void acceptCodeIfCorrect(Player player, String code) {

        if (code.equalsIgnoreCase(player.getName())) {
            MessageSender.sendMessage(player, "referralCodeIsPlayerName");
            return;
        }

        databaseManager.isPlayerExists(code).thenAccept(exist -> {

            if (!exist) {
                MessageSender.sendMessage(player, "referralCodeNotFound");
                return;
            }

            PlayerCache playerCache = playerCacheStorage.getPlayerCache(player.getUniqueId());
            PlayerModel playerModel = playerCache.getPlayerModel();
            playerModel.setReferName(code);
            playerCache.setPlayerModel(playerModel);

            MessageSender.sendMessage(player, "referralCodeSet");

        });

    }

}
