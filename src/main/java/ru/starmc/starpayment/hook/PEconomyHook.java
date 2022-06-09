package ru.starmc.starpayment.hook;

import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import ru.soknight.peconomy.api.PEconomyAPI;
import ru.soknight.peconomy.database.model.WalletModel;


public class PEconomyHook {

    private final PEconomyAPI api;
    private final String currency;

    public PEconomyHook(Configuration config, Server server) {

        if (server.getPluginManager().isPluginEnabled("PEconomy")) {
            this.api = PEconomyAPI.get();
        } else {
            this.api = null;
        }

        this.currency = config.getString("payment.currency");

    }

    public boolean isInitialized() {
        return api != null;
    }

    public boolean addCurrencyToPlayer(String playerName, float amount) {

        WalletModel wallet = api.getWallet(playerName);
        if (wallet == null) return false;

        wallet.addAmount(this.currency, amount);
        this.api.updateWallet(wallet);

        return true;

    }

}
