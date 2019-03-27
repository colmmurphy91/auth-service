package com.cluborg.authservice.configuration;

import com.cluborg.authservice.security.AuthoritiesConstants;
import com.cluborg.authservice.user.domain.Authority;
import com.cluborg.authservice.user.domain.User;
import com.cluborg.authservice.user.repository.AuthorityRepository;
import com.cluborg.authservice.user.repository.UserRepository;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SetupInitMigration implements SmartInitializingSingleton {

    private final UserRepository users;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public SetupInitMigration(UserRepository users, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void afterSingletonsInstantiated() {

        this.users.deleteAll().block();
        this.authorityRepository.deleteAll().block();

        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthoritiesConstants.ADMIN);
        Authority userAuthority = new Authority();
        userAuthority.setName(AuthoritiesConstants.USER);

        authorityRepository.save(adminAuthority).block();
        authorityRepository.save(userAuthority).block();

        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setEnabled(true);//password: password
        adminUser.setAccountNonLocked(true);
        adminUser.getAuthorities().add(adminAuthority);
        adminUser.getAuthorities().add(userAuthority);

        this.users.save(adminUser).block();
    }
}