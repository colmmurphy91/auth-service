package com.cluborg.authservice.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddUserDto {

    String username, password;

    String authority;
}
