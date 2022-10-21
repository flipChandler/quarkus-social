package io.github.flipchandler.quarkussocial.repository;

import io.github.flipchandler.quarkussocial.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
