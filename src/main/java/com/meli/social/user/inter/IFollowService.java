package com.meli.social.user.inter;

import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.model.User;

public interface IFollowService {

    boolean isFollowing(Integer followerId, Integer followedId);

    User followUser(Integer followerId, Integer followedId);

    User unfollowUser(Integer followerId, Integer followedId);

    UserSimpleDTO returnUserWithFollowerCounter(Integer userId);
}
