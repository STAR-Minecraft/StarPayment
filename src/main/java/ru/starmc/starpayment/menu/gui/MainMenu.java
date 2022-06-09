package ru.starmc.starpayment.menu.gui;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.starmc.starpayment.cache.PlayerCache;
import ru.starmc.starpayment.cache.PlayerCacheStorage;
import ru.starmc.starpayment.common.Colorizer;
import ru.starmc.starpayment.hook.PEconomyHook;
import ru.starmc.starpayment.menu.anvil.AnvilMenu;
import ru.starmc.starpayment.menu.anvil.ChangeReferralAnvil;
import ru.starmc.starpayment.menu.anvil.CreatePaymentAnvil;
import ru.starmc.starpayment.menu.gui.triumph.builder.item.ItemBuilder;
import ru.starmc.starpayment.menu.gui.triumph.guis.Gui;
import ru.starmc.starpayment.menu.gui.triumph.guis.GuiItem;
import ru.starmc.starpayment.message.MessageSender;
import ru.starmc.starpayment.withdraw.BonusWithdraw;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainMenu implements GuiMenu {

    private static final String CONFIG_PATH = "menu.main";

    MenuClickDelay menuClickDelay;
    PEconomyHook pEconomyHook;
    PlayerCacheStorage playerCacheStorage;
    AnvilMenu changeReferralAnvil;
    AnvilMenu createPaymentAnvil;
    AnvilMenu createWithdrawAnvil;

    String title;
    int rows;

    boolean fillerEnabled;
    GuiItem fillerItem;
    ItemBuilder infoItem;
    int infoItemSlot;
    ItemBuilder buyItem;
    int buyItemSlot;
    ItemBuilder referralSystemItem;
    int referralSystemItemSlot;

    public MainMenu(Configuration config, MenuClickDelay menuClickDelay, PEconomyHook pEconomyHook, PlayerCacheStorage playerCacheStorage, AnvilMenu changeReferralAnvil, AnvilMenu createPaymentAnvil, AnvilMenu createWithdrawAnvil) {

        this.menuClickDelay = menuClickDelay;
        this.pEconomyHook = pEconomyHook;
        this.playerCacheStorage = playerCacheStorage;
        this.changeReferralAnvil = changeReferralAnvil;
        this.createPaymentAnvil = createPaymentAnvil;
        this.createWithdrawAnvil = createWithdrawAnvil;

        this.title = Colorizer.colorizeString(config.getString(CONFIG_PATH + ".title"));
        this.rows = config.getInt(CONFIG_PATH + ".rows");

        this.fillerEnabled = config.getBoolean(CONFIG_PATH + ".filler.enabled");
        this.fillerItem = ItemBuilder
                .from(Material.valueOf(config.getString(CONFIG_PATH + ".filler.material")))
                .name(" ").asGuiItem();

        this.infoItem = ItemBuilder
                .from(Material.valueOf(config.getString(CONFIG_PATH + ".infoItem.material")))
                .name(Colorizer.colorizeString(config.getString(CONFIG_PATH + ".infoItem.name")))
                .lore(Colorizer.colorizeList(config.getStringList(CONFIG_PATH + ".infoItem.lore")));
        this.infoItemSlot = config.getInt(CONFIG_PATH + ".infoItem.slot");

        this.buyItem = ItemBuilder
                .from(Material.valueOf(config.getString(CONFIG_PATH + ".buyItem.material")))
                .name(Colorizer.colorizeString(config.getString(CONFIG_PATH + ".buyItem.name")))
                .lore(Colorizer.colorizeList(config.getStringList(CONFIG_PATH + ".buyItem.lore")));
        this.buyItemSlot = config.getInt(CONFIG_PATH + ".buyItem.slot");

        this.referralSystemItem = ItemBuilder
                .from(Material.valueOf(config.getString(CONFIG_PATH + ".referralSystemItem.material")))
                .name(Colorizer.colorizeString(config.getString(CONFIG_PATH + ".referralSystemItem.name")))
                .lore(Colorizer.colorizeList(config.getStringList(CONFIG_PATH + ".referralSystemItem.lore")));
        this.referralSystemItemSlot = config.getInt(CONFIG_PATH + ".referralSystemItem.slot");

    }

    @Override
    public void open(Player player) {

        Gui gui = Gui.gui()
                .title(this.title)
                .rows(this.rows)
                .create();

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        if (this.fillerEnabled)
            gui.getFiller().fill(this.fillerItem);

        PlayerCache playerCache = playerCacheStorage.getPlayerCache(player.getUniqueId());

        GuiItem infoItem = this.infoItem.asGuiItem(event -> {
            if (menuClickDelay.isPlayerInDelay(event.getWhoClicked().getUniqueId())) return;
            menuClickDelay.addPlayer(event.getWhoClicked().getUniqueId());
            this.changeReferralAnvil.open((Player) event.getWhoClicked());
        });

        infoItem.setItemStack(replaceItemLorePlaceholders(
                infoItem.getItemStack(), playerCache));

        GuiItem buyItem = this.buyItem.asGuiItem(event -> {
            if (menuClickDelay.isPlayerInDelay(event.getWhoClicked().getUniqueId())) return;
            menuClickDelay.addPlayer(event.getWhoClicked().getUniqueId());
            this.createPaymentAnvil.open((Player) event.getWhoClicked());
        });

        buyItem.setItemStack(replaceItemLorePlaceholders(
                buyItem.getItemStack(), playerCache));

        GuiItem referralSystemItem = this.referralSystemItem.asGuiItem(event -> {

            if (event.getClick() == ClickType.LEFT) {

                if (menuClickDelay.isPlayerInDelay(event.getWhoClicked().getUniqueId())) return;
                menuClickDelay.addPlayer(event.getWhoClicked().getUniqueId());

                int bonus = (int) playerCache.getPlayerModel().getBonus();
                pEconomyHook.addCurrencyToPlayer(player.getName(), bonus);

                MessageSender.sendMessage(player, "bonusTook");

            }

            if (event.getClick() == ClickType.RIGHT) {

                if (playerCache.getPlayerModel().getBonus() < BonusWithdraw.getMinSum()) {
                    MessageSender.sendMessage(player, "notEnoughBonusToWithdraw");
                    return;
                }

                createWithdrawAnvil.open(player);

            }

        });

        referralSystemItem.setItemStack(replaceItemLorePlaceholders(
                referralSystemItem.getItemStack(), playerCache));

        gui.setItem(infoItemSlot, infoItem);
        gui.setItem(buyItemSlot, buyItem);
        gui.setItem(referralSystemItemSlot, referralSystemItem);

        gui.open(player);

    }

    private ItemStack replaceItemLorePlaceholders(ItemStack itemStack, PlayerCache playerCache) {

        if (itemStack.getLore() == null) return itemStack;

        List<String> lore = new ArrayList<>();

        itemStack.getLore().forEach(line -> {
            lore.add(replacePlaceholders(line, playerCache));
        });

        itemStack.setLore(lore);

        return itemStack;

    }

    private String replacePlaceholders(String toReplace, PlayerCache playerCache) {

        return toReplace
                .replace("{refer}", playerCache.getPlayerModel().getReferName() == null
                        ? "-" : playerCache.getPlayerModel().getReferName())
                .replace("{own_refer_bonus}", playerCache.getReferralBonus().getReferral() + "")
                .replace("{refer_bonus}", playerCache.getReferralBonus().getOwn() + "")
                .replace("{code}", playerCache.getPlayerModel().getPlayerName())
                .replace("{own_bonus}", playerCache.getOwnBonus().getOwn() + "")
                .replace("{referral_bonus}", playerCache.getOwnBonus().getReferral() + "")
                .replace("{bonus}", playerCache.getPlayerModel().getBonus() + "");

    }

}
