package com.meli.social.user.inter;

import com.meli.social.user.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;


public interface IUserRepository {

    User save(User user);

    Optional<User> findById(Integer id);

    long count();

}