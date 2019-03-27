package com.cluborg.authservice;

import com.cluborg.authservice.configuration.SecurityConfiguration;
import com.cluborg.authservice.security.ReactiveUserDetailsServiceImpl;
import com.cluborg.authservice.security.UnauthorizedAuthenticationEntryPoint;
import com.cluborg.authservice.security.controller.LoginHandler;
import com.cluborg.authservice.security.controller.LoginRestConfiguration;
import com.cluborg.authservice.security.dto.AddUserDto;
import com.cluborg.authservice.security.dto.LoginVM;
import com.cluborg.authservice.security.jwt.JWTToken;
import com.cluborg.authservice.security.jwt.TokenProvider;
import com.cluborg.authservice.user.domain.Authority;
import com.cluborg.authservice.user.domain.User;
import com.cluborg.authservice.user.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest
@Import({LoginRestConfiguration.class, LoginHandler.class, TokenProvider.class, ReactiveUserDetailsServiceImpl.class,
        UnauthorizedAuthenticationEntryPoint.class,
        SecurityConfiguration.class})
@RunWith(SpringRunner.class)
public class AuthControllerTest {

    @Autowired
    ApplicationContext context;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void givenAValidUserATokenIsReturned(){
        Set<Authority> auth = new HashSet<>();
        User user = new User("id", "ColmMurphy", passwordEncoder.encode("Password"), auth, true, true);

        LoginVM loginVM = new LoginVM("ColmMurphy", "Password");


        Mockito.when(userRepository.findByUsername("ColmMurphy")).thenReturn(Mono.just(user));

        webTestClient
                .post()
                .uri("/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginVM), LoginVM.class)
                .exchange()
                .expectStatus()
                .isOk()
        .expectBody(JWTToken.class);
    }

    @Test
    public void givenAValidUserWithAnInvalidPassword(){
        Set<Authority> auth = new HashSet<>();
        User user = new User("id", "ColmMurphy", passwordEncoder.encode("Password"), auth, true, true);

        LoginVM loginVM = new LoginVM("ColmMurphy", "Password1");


        Mockito.when(userRepository.findByUsername("ColmMurphy")).thenReturn(Mono.just(user));

        webTestClient
                .post()
                .uri("/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginVM), LoginVM.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    public void givenANonExistentUserWithAnInvalidPassword(){

        LoginVM loginVM = new LoginVM("ColmMurphy", "Password1");

        Mockito.when(userRepository.findByUsername("ColmMurphy")).thenReturn(Mono.empty());

        webTestClient
                .post()
                .uri("/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginVM), LoginVM.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void givenANewUserUserCanBeStoredInAuthService(){
        Set<Authority> auth = new HashSet<>();
        AddUserDto addUserDto = new AddUserDto("Colm", "Password", "ROLE_ADMIN");

        User user = new User(null, addUserDto.getUsername(), addUserDto.getPassword(), auth, true, true);

        Mockito.when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        webTestClient
                .post()
                .uri("/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(addUserDto), AddUserDto.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
