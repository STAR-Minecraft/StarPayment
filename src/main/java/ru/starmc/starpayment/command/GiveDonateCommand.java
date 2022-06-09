package ru.starmc.starpayment.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.starmc.starpayment.bonus.BonusContainer;
import ru.starmc.starpayment.cache.PlayerCache;
import ru.starmc.starpayment.cache.PlayerCacheStorage;
import ru.starmc.starpayment.database.DatabaseManager;
import ru.starmc.starpayment.database.model.PlayerModel;
import ru.starmc.starpayment.hook.PEconomyHook;
import ru.starmc.starpayment.message.MessageSender;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiveDonateCommand implements CommandExecutor, TabCompleter {

    private static final String COMMAND_NAME = "starpaymentgive";
    private static final String USE_PERMISSION = "starpayment.give";

    PlayerCacheStorage playerCacheStorage;
    BonusContainer bonusContainer;
    PEconomyHook pEconomyHook;
    DatabaseManager databaseManager;

    public void register(JavaPlugin javaPlugin) {
        javaPlugin.getCommand(COMMAND_NAME).setExecutor(this);
        javaPlugin.getCommand(COMMAND_NAME).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission(USE_PERMISSION)) {
            MessageSender.sendMessage(sender, "noPerm");
            return true;
        }

        if (args.length < 2) {
            MessageSender.sendMessage(sender, "giveDonateHelp");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessageSender.sendMessage(sender, "inputNotNumber");
            return true;
        }
        String playerName = args[0];

        giveDonate(playerName, amount);
        MessageSender.sendMessage(sender, "giveDonateSuccessful");

        MessageSender.sendMessage(sender, "giveDonateBonusLoad");
        databaseManager.isPlayerExists(playerName).thenAccept(exists -> {

            if (!exists) {
                MessageSender.sendMessage(sender, "giveDonatePlayerNotFound");
                return;
            }

            databaseManager.getPlayerModel(playerName).thenAccept(playerModel -> {

                String referName = playerModel.getReferName();

                if (playerCacheStorage.isPlayerCacheLoad(playerModel.getPlayerUUID())) {
                    referName = playerCacheStorage.getPlayerCache(playerModel.getPlayerUUID())
                            .getPlayerModel().getReferName();
                }

                if (referName == null) {
                    MessageSender.sendMessage(sender, "giveDonatePlayerNotHaveRefer");
                    return;
                }

                databaseManager.getPlayerModel(referName).thenAccept(referModel -> {

                    bonusContainer.getPlayerBonus(referModel.getPlayerUUID()).thenAccept(bonus -> {

                        giveBonus(
                                playerName,
                                playerModel.getPlayerUUID(),
                                bonus.calculateReferBonus(amount));

                        giveBonus(
                                referModel.getPlayerName(),
                                referModel.getPlayerUUID(),
                                bonus.calculateOwnBonus(amount));

                        MessageSender.sendMessage(sender, "giveDonateBonusSuccessful");

                    });

                });

            });

        });

        return true;

    }

    private void giveDonate(String playerName, int amount) {
        pEconomyHook.addCurrencyToPlayer(playerName, amount);
    }

    private void giveBonus(String playerName, UUID playerUUID, int amount) {

        if (playerCacheStorage.isPlayerCacheLoad(playerUUID)) {
            PlayerCache playerCache = playerCacheStorage.getPlayerCache(playerUUID);
            PlayerModel playerModel = playerCache.getPlayerModel();
            playerModel.setBonus(playerModel.getBonus() + amount);
            return;
        }

        databaseManager.getPlayerModel(playerName).thenAccept(playerModel -> {
            playerModel.setBonus(playerModel.getBonus() + amount);
            databaseManager.setPlayerModel(playerModel);
        });

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (!sender.hasPermission(USE_PERMISSION)) return List.of("Нет прав");

        if (args.length == 1) return List.of("Ник");

        if (args.length == 2) return List.of("50", "100", "200");

        return null;

    }

}
