package ru.starmc.starpayment.cache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.starmc.starpayment.bonus.Bonus;
import ru.starmc.starpayment.database.model.PlayerModel;

@AllArgsConstructor
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerCache {

    PlayerModel playerModel;
    Bonus ownBonus;
    Bonus referralBonus;

}
