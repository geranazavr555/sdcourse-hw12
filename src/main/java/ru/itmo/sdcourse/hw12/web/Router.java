package ru.itmo.sdcourse.hw12.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class Router {
    @Bean
    public RouterFunction<ServerResponse> routeProducts(ProductsHandler productsHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/products"), productsHandler::getProducts)
                .andRoute(RequestPredicates.POST("/products"), productsHandler::addProduct);
    }

    @Bean
    public RouterFunction<ServerResponse> routeRegisterUser(UsersHandler usersHandler) {
        return RouterFunctions.route(RequestPredicates.POST("/users"), usersHandler::registerUser);
    }
}
