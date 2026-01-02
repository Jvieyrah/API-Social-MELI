package com.meli.social.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.meli.social.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleDTO {
    private Integer userId;
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer followersCount;

    public UserSimpleDTO(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public static UserSimpleDTO fromUser(User user) {
        return new UserSimpleDTO(user.getUserId(), user.getUserName());
    }

    public static UserSimpleDTO fromUserWithFollowers(User user) {
        return new UserSimpleDTO(user.getUserId(), user.getUserName(), user.getFollowersCount());
    }
}