package com.meli.social.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meli.social.user.impl.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleDTO {
    private Integer userId;
    private String userName;

    public static UserSimpleDTO fromUser(User user) {
        return new UserSimpleDTO(user.getUserId(), user.getUserName());
    }
}