package ru.starmc.starpayment.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "star_payment_players")
public final class PlayerModel {

    public static final String COLUMN_PLAYER_NAME = "player_name";
    public static final String COLUMN_PLAYER_UUID = "player_uuid";
    public static final String COLUMN_REFER_NAME = "refer_name";
    public static final String COLUMN_BONUS = "bonus";

    @DatabaseField(columnName = COLUMN_PLAYER_NAME, id = true, canBeNull = false)
    private String playerName;
    @DatabaseField(columnName = COLUMN_PLAYER_UUID)
    private UUID playerUUID;
    @DatabaseField(columnName = COLUMN_REFER_NAME)
    private String referName;
    @DatabaseField(columnName = COLUMN_BONUS)
    private double bonus;

    public PlayerModel(String playerName, UUID playerUUID) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

}
