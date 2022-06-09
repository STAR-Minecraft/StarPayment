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
import ru.starmc.starpayment.database.model.PlayerModel;
import ru.starmc.starpayment.message.MessageSender;
import ru.starmc.starpayment.withdraw.BonusWithdraw;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateWithdrawAnvil implements AnvilMenu {

    private static final String CONFIG_PATH = "anvil.createWithdrawRequest";

    JavaPlugin pluginInstance;
    PlayerCacheStorage playerCacheStorage;

    String title;
    String text;

    public CreateWithdrawAnvil(Configuration config, PlayerCacheStorage playerCacheStorage, JavaPlugin pluginInstance) {

        this.playerCacheStorage = playerCacheStorage;
        this.pluginInstance = pluginInstance;

        this.title = Colorizer.colorizeString(config.getString(CONFIG_PATH + ".title"));
        this.text = Colorizer.colorizeString(config.getString(CONFIG_PATH + ".text"));

    }

    @Override
    public void open(Player player) {

        new AnvilGUI.Builder()
                .text(this.text)
                .title(this.title)
                .plugin(this.pluginInstance)
                .onComplete((user, text) -> {

                    PlayerCache playerCache = playerCacheStorage.getPlayerCache(player.getUniqueId());
                    PlayerModel playerModel = playerCache.getPlayerModel();

                    BonusWithdraw.createWithdrawRequest(player.getName(), text, (int) playerModel.getBonus());

                    playerModel.setBonus(0);
                    playerCache.setPlayerModel(playerModel);

                    MessageSender.sendMessage(player, "withdrawRequestCreated");

                    return AnvilGUI.Response.close();

                })
                .open(player);

    }

}
