package ru.itmo.sdcourse.hw12;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.itmo.sdcourse.hw12.model.Product;
import ru.itmo.sdcourse.hw12.repository.ProductRepository;
import ru.itmo.sdcourse.hw12.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
class Hw12ApplicationTests {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void initTestMongo() {
        userRepository.deleteAll().block();
        productRepository.deleteAll().block();
    }

    @AfterEach
    public void shutdownTestMongo() {
        userRepository.deleteAll().block();
        productRepository.deleteAll().block();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void noProducts() {
        webClient.get().uri("/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Product>>() {
                }).isEqualTo(Collections.emptyList());
    }

    @Test
    public void saveProduct() {
        webClient.post().uri("/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

        webClient.post().uri("/products?name=abacaba")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

        webClient.post().uri("/products?usdPrice100=12345")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

        webClient.post().uri("/products?usdPrice100=abacaba&name=abacaba")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

        webClient.post().uri("/products?usdPrice100=12345&name=abacaba")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class).value(product -> {
                    Assertions.assertEquals(12345L, product.getPriceUsd100());
                    Assertions.assertEquals("abacaba", product.getName());
                });
    }

    @Test
    public void getProducts() {
        webClient.post().uri("/products?usdPrice100=12345&name=abacaba")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class).value(product -> {
                    Assertions.assertEquals(12345L, product.getPriceUsd100());
                    Assertions.assertEquals("abacaba", product.getName());
                });

        webClient.get().uri("/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Product>>() {
                }).value(products -> {
                    Assertions.assertEquals(1, products.size());
                });
    }
}
