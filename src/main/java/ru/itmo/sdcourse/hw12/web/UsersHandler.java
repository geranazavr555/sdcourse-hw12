package ru.itmo.sdcourse.hw12.web;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.itmo.sdcourse.hw12.model.Currency;
import ru.itmo.sdcourse.hw12.model.User;
import ru.itmo.sdcourse.hw12.repository.UserRepository;

import java.util.Arrays;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UsersHandler {
    private final UserRepository userRepository;

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        var userCurrency = request.queryParam("currency")
                .filter(currency ->
                        Arrays.stream(Currency.values())
                                .map(Enum::name)
                                .anyMatch(currency1 -> currency1.equals(currency)))
                .map(Currency::valueOf).orElse(Currency.RUB);
        Mono<User> newUser = userRepository.save(new User(UUID.randomUUID(), userCurrency));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(newUser, User.class));
    }
}
