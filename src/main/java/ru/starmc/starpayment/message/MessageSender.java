package ru.starmc.starpayment.message;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import ru.starmc.starpayment.common.Colorizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageSender {

    private static final String MESSAGES_CONFIG_PATH = "messages.";
    private static Configuration config;

    public static void initialize(Configuration configuration) {
        config = configuration;
    }

    public static void sendMessage(CommandSender sender, String messageName, Map<String, String> placeholderReplacer) {
        getColorizedMessage(messageName).forEach(line -> {
            for (String placeholder : placeholderReplacer.keySet())
                line = line.replace(placeholder, placeholderReplacer.get(placeholder));
            sender.sendMessage(line);
            System.out.println(line);
        });
    }

    public static void sendMessage(CommandSender sender, String messageName) {
        getColorizedMessage(messageName).forEach(sender::sendMessage);
    }

    public static List<String> getColorizedMessage(String messageName) {
        return new ArrayList<>(Colorizer.colorizeList(config.getStringList(MESSAGES_CONFIG_PATH + messageName)));
    }

}
