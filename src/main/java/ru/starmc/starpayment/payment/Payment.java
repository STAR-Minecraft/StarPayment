package ru.starmc.starpayment.payment;

import java.util.concurrent.CompletableFuture;

public interface Payment {

    CompletableFuture<String> createPaymentLink(String playerName, double amount);

}
