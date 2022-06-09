package ru.starmc.starpayment.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.starmc.starpayment.cache.PlayerCacheStorage;
import ru.starmc.starpayment.menu.gui.GuiMenu;
import ru.starmc.starpayment.message.MessageSender;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DonateCommand implements CommandExecutor, TabCompleter {

    private static final String COMMAND_NAME = "starpayment";

    PlayerCacheStorage playerCacheStorage;
    GuiMenu menu;

    public void register(JavaPlugin javaPlugin) {
        javaPlugin.getCommand(COMMAND_NAME).setExecutor(this);
        javaPlugin.getCommand(COMMAND_NAME).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            MessageSender.sendMessage(sender, "console");
            return true;
        }

        Player player = (Player) sender;

        if (!playerCacheStorage.isPlayerCacheLoad(player.getUniqueId())) {
            MessageSender.sendMessage(sender, "load");
            return true;
        }

        menu.open(player);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
