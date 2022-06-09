package ru.starmc.starpayment.withdraw;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class WithdrawRequestData {

    private static final String FILE_NAME = "withdraws.yml";

    final JavaPlugin javaPlugin;

    File file;
    YamlConfiguration configuration;

    public WithdrawRequestData(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.createFile();
    }

    private void createFile() {

        file = new File(javaPlugin.getDataFolder(), FILE_NAME);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            javaPlugin.saveResource(FILE_NAME, false);
        }

        configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public YamlConfiguration get() {
        return this.configuration;
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
