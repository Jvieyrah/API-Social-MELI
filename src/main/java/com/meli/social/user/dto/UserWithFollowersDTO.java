package com.meli.social.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.meli.social.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithFollowersDTO {
    private Integer userId;
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<UserSimpleDTO> followers;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<UserSimpleDTO> followed;

    public static UserWithFollowersDTO withFollowers(User user, List<UserSimpleDTO> followers) {
        UserWithFollowersDTO dto = new UserWithFollowersDTO();
        dto.userId = user.getUserId();
        dto.userName = user.getUserName();
        dto.followers = followers;
        return dto;
    }

    public static UserWithFollowersDTO withFollowed(User user, List<UserSimpleDTO> followed) {
        UserWithFollowersDTO dto = new UserWithFollowersDTO();
        dto.userId = user.getUserId();
        dto.userName = user.getUserName();
        dto.followed = followed;
        return dto;
    }
}
