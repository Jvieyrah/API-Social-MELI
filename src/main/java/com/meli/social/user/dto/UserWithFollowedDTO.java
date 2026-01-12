package com.meli.social.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.meli.social.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Manter metodos Lombok para herança
@JsonPropertyOrder({"userId", "userName", "followed"})
public class UserWithFollowedDTO extends UserSimpleDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserSimpleDTO> followed;

    public static UserWithFollowedDTO withFollowed(User user, List<UserSimpleDTO> followed) {
        UserWithFollowedDTO dto = new UserWithFollowedDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setFollowed(followed);
        return dto;
    }
}
