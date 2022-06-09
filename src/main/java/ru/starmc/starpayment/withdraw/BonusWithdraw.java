package ru.starmc.starpayment.withdraw;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.Configuration;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BonusWithdraw {

    static WithdrawRequestData data;
    @Getter static boolean enabled;
    @Getter static int minSum;

    public static void initialize(WithdrawRequestData withdrawRequestData, Configuration config) {

        data = withdrawRequestData;

        enabled = config.getBoolean("bonusWithdraw.enabled");
        minSum = config.getInt("bonusWithdraw.minSum");

    }

    public static void createWithdrawRequest(String playerName, String comment, int amount) {

        UUID requestUUID = UUID.randomUUID();

        data.get().set("requests." + requestUUID + ".player", playerName);
        data.get().set("requests." + requestUUID + ".comment", comment);
        data.get().set("requests." + requestUUID + ".sum", amount);

        data.save();

    }

}
