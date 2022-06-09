package ru.starmc.starpayment.menu.anvil;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.starmc.starpayment.common.Colorizer;
import ru.starmc.starpayment.message.MessageSender;
import ru.starmc.starpayment.payment.Payment;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreatePaymentAnvil implements AnvilMenu {

    private static final String CONFIG_PATH = "anvil.createPayment";
    private static final String MIN_SUM_CONFIG_PATH = "payment.minSum";

    Payment payment;
    JavaPlugin pluginInstance;

    String title;
    String text;

    int minSum;

    public CreatePaymentAnvil(Configuration config, Payment payment, JavaPlugin pluginInstance) {

        this.payment = payment;
        this.pluginInstance = pluginInstance;

        this.title = Colorizer.colorizeString(config.getString(CONFIG_PATH + ".title"));
        this.text = config.getString(CONFIG_PATH + ".text");

        this.minSum = config.getInt(MIN_SUM_CONFIG_PATH);

    }

    @Override
    public void open(Player player) {

        new AnvilGUI.Builder()
                .text(this.text)
                .title(this.title)
                .plugin(this.pluginInstance)
                .onComplete((user, text) -> {

                    int sum = parseSum(text);

                    if (sum < 0) {
                        MessageSender.sendMessage(user, "inputNotNumber");
                        return AnvilGUI.Response.close();
                    }

                    if (sum < this.minSum) {
                        MessageSender.sendMessage(user, "numberLessThenMin",
                                Map.of("{min_sum}", minSum + ""));
                        return AnvilGUI.Response.close();
                    }

                    MessageSender.sendMessage(user, "linkLoad");

                    payment.createPaymentLink(user.getName(), sum).thenAccept(link -> {
                        MessageSender.sendMessage(user, "linkCreated", Map.of("{link}", link));
                    });

                    return AnvilGUI.Response.close();

                })
                .open(player);

    }

    private int parseSum(String input) {

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignore) {
            return -1;
        }

    }

}
