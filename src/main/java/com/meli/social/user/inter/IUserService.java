package com.meli.social.user.inter;

import com.meli.social.user.dto.UserDTO;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.dto.UserWithFollowedDTO;
import com.meli.social.user.dto.UserWithFollowersDTO;
import com.meli.social.user.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    UserSimpleDTO createUser(String userName);

    UserWithFollowersDTO getFollowers(Integer userId);

    UserWithFollowersDTO getFollowers(Integer userId, String order);

    UserWithFollowersDTO getFollowers(Integer userId, String order, int page, int size);

    UserWithFollowedDTO getFollowing(Integer userId, String order);

    UserWithFollowedDTO getFollowing(Integer userId, String order, int page, int size);

    List<UserDTO> getTopUsers(int limit);

}