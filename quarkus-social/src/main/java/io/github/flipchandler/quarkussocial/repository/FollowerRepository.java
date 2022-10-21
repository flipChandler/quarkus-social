package io.github.flipchandler.quarkussocial.repository;

import io.github.flipchandler.quarkussocial.domain.Follower;
import io.github.flipchandler.quarkussocial.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean isFollowing(User follower, User user) {
        Map<String, Object> params = Parameters.with("follower", follower).and("user", user).map();

        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);

        return query
                .firstResultOptional()
                .isPresent();
    }

    public List<Follower> getAllByUser(Long userId) {
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("followerId", followerId);
        params.put("userId", userId);
        delete("follower.id = :followerId and user.id = :userId", params);
    }
}
