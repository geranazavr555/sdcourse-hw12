package ru.itmo.sdcourse.hw12.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.itmo.sdcourse.hw12.model.User;

import java.util.UUID;

public interface UserRepository extends ReactiveMongoRepository<User, UUID> {
}
