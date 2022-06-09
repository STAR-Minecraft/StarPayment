package ru.starmc.starpayment.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для генерации исходного подключения к базе данных,
 * на основе данных из конфига
 */
public class ConnectionGenerator {

    private final File dataFolder;
    private final Configuration config;

    public ConnectionGenerator(File dataFolder, Configuration config) {
        this.dataFolder = dataFolder;
        this.config = config;
    }

    /**
     * Метод генерирует объект подключения к базе данных
     * ({@link JdbcConnectionSource JdbcConnectionSource.class}) на основе данных из конфига
     * @return Объект подключения к базе данных
     */
    public JdbcConnectionSource getConnectionSource() throws SQLException {

        switch (config.getString("database.type").toLowerCase()) {

            case "sqlite":
                return new JdbcConnectionSource(
                        "jdbc:sqlite:" + this.dataFolder + File.separator + config.getString("database.sqlite.file"));

            case "mysql":
                return new JdbcConnectionSource(
                        String.format("jdbc:mysql://%s:%d/%s%s",
                                config.getString("database.mysql.host"),
                                config.getInt("database.mysql.port"),
                                config.getString("database.mysql.name"),
                                formatParameters(config.getStringList("database.mysql.params"))),
                        config.getString("database.mysql.user"),
                        config.getString("database.mysql.password"));

            case "postgresql":
                return new JdbcConnectionSource(String.format("jdbc:postgresql://%s:%d/%s%s",
                        config.getString("database.postgresql.host"),
                        config.getInt("database.postgresql.port"),
                        config.getString("database.postgresql.name"),
                        formatParameters(config.getStringList("database.postgresql.params"))),
                        config.getString("database.postgresql.user"),
                        config.getString("database.postgresql.password"));

        }

        return null;

    }

    private String formatParameters(List<String> parameters) {
        if(parameters == null || parameters.isEmpty())
            return "";

        String joined = parameters.stream()
                .map(this::urlEncode)
                .collect(Collectors.joining("&"));

        return "?" + joined;
    }

    private String urlEncode(String source) {
        return URLEncoder.encode(source, StandardCharsets.UTF_8);
    }

}
