package com.cluborg.authservice.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Size(min = 60, max = 60)
    private String password;

    @JsonIgnore
    private Set<Authority> authorities = new HashSet<>();

    private boolean enabled;

    private boolean isAccountNonLocked;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
