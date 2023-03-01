package ru.itmo.sdcourse.hw12.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.itmo.sdcourse.hw12.model.Product;

import java.util.UUID;

public interface ProductRepository extends ReactiveMongoRepository<Product, UUID> {
}
