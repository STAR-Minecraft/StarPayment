package ru.starmc.starpayment.bonus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bonus {

    int own;
    int referral;

    public int calculateOwnBonus(int value) {
        return ((value / 100) * own);
    }

    public int calculateReferBonus(int value) {
        return ((value / 100) * referral);
    }

}
