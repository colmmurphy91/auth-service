package com.cluborg.authservice.security.jwt;

import lombok.*;

/**
 * @author duc-d
 */
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTToken {
	private String token;
}
