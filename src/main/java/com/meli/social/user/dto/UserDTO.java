package com.meli.social.user.dto;

import com.meli.social.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer userId;
    private String userName;
    private Integer followersCount;
    private List<UserSimpleDTO> followers;
    private List<UserSimpleDTO> following;

    // Construtor simplificado (sem relacionamentos)
    public UserDTO(Integer userId, String userName, Integer followersCount) {
        this.userId = userId;
        this.userName = userName;
        this.followersCount = followersCount;
    }

    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getFollowersCount()
        );
    }
}