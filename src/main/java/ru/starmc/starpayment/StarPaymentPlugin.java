package ru.starmc.starpayment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;
import ru.starmc.starpayment.bonus.BonusContainer;
import ru.starmc.starpayment.cache.PlayerCacheListener;
import ru.starmc.starpayment.cache.PlayerCacheStorage;
import ru.starmc.starpayment.command.DonateCommand;
import ru.starmc.starpayment.command.GiveDonateCommand;
import ru.starmc.starpayment.database.ConnectionGenerator;
import ru.starmc.starpayment.database.DatabaseManager;
import ru.starmc.starpayment.hook.LuckPermsHook;
import ru.starmc.starpayment.hook.PEconomyHook;
import ru.starmc.starpayment.menu.anvil.AnvilMenu;
import ru.starmc.starpayment.menu.anvil.ChangeReferralAnvil;
import ru.starmc.starpayment.menu.anvil.CreatePaymentAnvil;
import ru.starmc.starpayment.menu.anvil.CreateWithdrawAnvil;
import ru.starmc.starpayment.menu.gui.GuiMenu;
import ru.starmc.starpayment.menu.gui.MainMenu;
import ru.starmc.starpayment.menu.gui.MenuClickDelay;
import ru.starmc.starpayment.message.MessageSender;
import ru.starmc.starpayment.payment.EasyDonatePayment;
import ru.starmc.starpayment.payment.Payment;
import ru.starmc.starpayment.withdraw.BonusWithdraw;
import ru.starmc.starpayment.withdraw.WithdrawRequestData;

import java.io.File;
import java.sql.SQLException;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class StarPaymentPlugin extends JavaPlugin {

    DatabaseManager databaseManager;
    PEconomyHook pEconomyHook;
    LuckPermsHook luckPermsHook;
    Payment payment;
    BonusContainer bonusContainer;
    PlayerCacheStorage playerCacheStorage;
    MenuClickDelay menuClickDelay;
    AnvilMenu changeReferralAnvilMenu;
    AnvilMenu createPaymentAnvilMenu;
    AnvilMenu createWithdrawAnvilMenu;
    GuiMenu mainMenu;

    @Override
    public void onEnable() {

        createConfig();

        MessageSender.initialize(this.getConfig());

        try {
            this.databaseManager = new DatabaseManager(new ConnectionGenerator(this.getDataFolder(), this.getConfig()));
        } catch (SQLException e) {
            this.getLogger().severe("Ошибка при создании базы данных.");
            throw new RuntimeException(e);
        }

        pEconomyHook = new PEconomyHook(this.getConfig(), this.getServer());
        if (!pEconomyHook.isInitialized()) {
            this.getLogger().severe("PEconomy не инициализирован!");
            this.getPluginLoader().disablePlugin(this);
        }

        luckPermsHook = new LuckPermsHook(this.getServer());
        if (!luckPermsHook.isInitialized()) {
            this.getLogger().severe("LuckPerms не инициализирован!");
            this.getPluginLoader().disablePlugin(this);
        }

        payment = new EasyDonatePayment(this.getConfig());


        bonusContainer = new BonusContainer(this.getConfig(), this.luckPermsHook);

        playerCacheStorage = new PlayerCacheStorage(this.databaseManager, this.bonusContainer);
        new PlayerCacheListener(this.playerCacheStorage).register(this);

        BonusWithdraw.initialize(new WithdrawRequestData(this), this.getConfig());

        menuClickDelay = new MenuClickDelay(20, this);

        changeReferralAnvilMenu = new ChangeReferralAnvil(this.getConfig(), this.databaseManager, this.playerCacheStorage, this);
        createPaymentAnvilMenu = new CreatePaymentAnvil(this.getConfig(), this.payment, this);
        createWithdrawAnvilMenu = new CreateWithdrawAnvil(this.getConfig(), this.playerCacheStorage, this);

        mainMenu = new MainMenu(this.getConfig(), this.menuClickDelay, this.pEconomyHook, this.playerCacheStorage, changeReferralAnvilMenu, createPaymentAnvilMenu, createWithdrawAnvilMenu);

        new DonateCommand(playerCacheStorage, mainMenu).register(this);
        new GiveDonateCommand(playerCacheStorage, bonusContainer, pEconomyHook, databaseManager).register(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createConfig() {

        File config = new File(getDataFolder(), "config.yml");
        if(!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }

        reloadConfig();

    }

}
