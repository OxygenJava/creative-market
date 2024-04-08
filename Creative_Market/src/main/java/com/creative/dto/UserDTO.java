package com.creative.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String e_mail;
    private String nickName;
    private String iconImage;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private String address;
    private Integer fansCount;
    private Integer FocusCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
