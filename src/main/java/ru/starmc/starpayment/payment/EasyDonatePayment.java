package ru.starmc.starpayment.payment;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.easydonate.easydonate4j.api.v3.client.EasyDonateClient;
import ru.easydonate.easydonate4j.exception.HttpRequestException;
import ru.easydonate.easydonate4j.exception.HttpResponseException;
import ru.easydonate.easydonate4j.http.client.jdk.legacy.JDKLegacyHttpClientService;
import ru.easydonate.easydonate4j.json.serialization.GsonSerializationService;

import java.util.concurrent.CompletableFuture;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EasyDonatePayment implements Payment {

    EasyDonateClient client;

    int productId;
    int serverId;
    String redirectURL;

    public EasyDonatePayment(Configuration config) {

        // Регистрация модулей EasyDonate4J
        JDKLegacyHttpClientService.registerIfNotRegisteredYet();
        GsonSerializationService.registerIfNotRegisteredYet();

        this.productId = config.getInt("payment.productId");
        this.serverId = config.getInt("payment.serverId");
        this.redirectURL = config.getString("payment.redirectURL");

        String accessKey = config.getString("payment.accessKey");
        this.client = EasyDonateClient.create(accessKey);

    }

    @Override
    public CompletableFuture<String> createPaymentLink(String playerName, double amount) {

        return CompletableFuture.supplyAsync(() -> {

            try {
                return client.createPayment()
                        .addProduct(productId, (int) amount)
                        .setServerId(serverId)
                        .setCustomer(playerName)
                        .setRedirectUrl(redirectURL)
                        .create().getUrl();
            } catch (HttpRequestException | HttpResponseException e) {
                e.printStackTrace();
                return "null";
            }

        });

    }

}
