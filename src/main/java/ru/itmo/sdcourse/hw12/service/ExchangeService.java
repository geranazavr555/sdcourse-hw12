package ru.itmo.sdcourse.hw12.service;

import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.itmo.sdcourse.hw12.model.Currency;

@Service
public class ExchangeService {
    private final WebClient webClient = WebClient.create("https://openexchangerates.org/api/latest.json?app_id=2502f96d9ec84e698d6e8b048837de17");

    public Mono<Long> convertFromUsd(long amount, Mono<Currency> currencyMono) {
        return currencyMono.flatMap(currency -> {
            if (currency == Currency.USD)
                return Mono.just(amount);

            return webClient.get()
                     .retrieve()
                     .bodyToMono(String.class)
                     .map(bodyString -> {
                         var root = JsonParser.parseString(bodyString).getAsJsonObject();
                         var rates = root.getAsJsonObject("rates");
                         var rate = rates.getAsJsonPrimitive(currency.name()).getAsDouble();
                         return StrictMath.round(rate * amount);
                     });
        });
    }
}
