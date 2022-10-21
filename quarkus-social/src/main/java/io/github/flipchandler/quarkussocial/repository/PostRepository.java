package io.github.flipchandler.quarkussocial.repository;

import io.github.flipchandler.quarkussocial.domain.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {
}
