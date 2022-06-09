package ru.starmc.starpayment.common;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class Colorizer {

    public static String colorizeString(String string) {
        return string != null && !string.isEmpty() ? ChatColor.translateAlternateColorCodes('&', string) : string;
    }

    public static List<String> colorizeList(List<String> list) {
        if (list != null && !list.isEmpty())
            return list.stream()
                    .map(Colorizer::colorizeString)
                    .collect(Collectors.toList());

        return list;
    }

}
