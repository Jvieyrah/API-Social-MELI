package com.meli.social.user.impl;

import com.meli.social.user.inter.IUserRepository;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements IUserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

}