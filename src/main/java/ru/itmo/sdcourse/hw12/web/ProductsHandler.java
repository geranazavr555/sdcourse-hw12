package ru.itmo.sdcourse.hw12.web;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.sdcourse.hw12.model.Currency;
import ru.itmo.sdcourse.hw12.model.Product;
import ru.itmo.sdcourse.hw12.model.User;
import ru.itmo.sdcourse.hw12.repository.ProductRepository;
import ru.itmo.sdcourse.hw12.repository.UserRepository;
import ru.itmo.sdcourse.hw12.service.ExchangeService;

import java.util.UUID;

@AllArgsConstructor
@Component
public class ProductsHandler {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ExchangeService exchangeService;

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        var name = request.queryParam("name");
        var usdPrice100 = request.queryParam("usdPrice100");
        var productMono = Mono.fromCallable(() -> {
            if (name.isEmpty() || usdPrice100.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both name and usdPrice100 must be present");
            try {
                long usdPrice100Long = usdPrice100.map(Long::parseLong).get();
                return new Product(UUID.randomUUID(), name.get(), usdPrice100Long);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }).flatMap(productRepository::save);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(productMono, Product.class));
    }


    public Mono<ServerResponse> getProducts(ServerRequest request) {
        var userCurrency = request.queryParam("userId")
                .map(UUID::fromString)
                .map(userRepository::findById)
                .map(monoUser -> monoUser.map(User::getCurrency))
                .orElseGet(() -> Mono.just(Currency.USD));

        var productAmounts = productRepository.findAll()
                .flatMap(product -> exchangeService.convertFromUsd(product.getPriceUsd100(), userCurrency));

        var products = Flux.zip(productRepository.findAll(), productAmounts,
                (product, amount) -> new Product(product.getId(), product.getName(), amount));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(products, new ParameterizedTypeReference<>() {
                }));
    }
}
