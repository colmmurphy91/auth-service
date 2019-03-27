package com.cluborg.authservice.security.controller;

import com.cluborg.authservice.security.dto.AddUserDto;
import com.cluborg.authservice.security.dto.LoginVM;
import com.cluborg.authservice.security.exception.UserDoesNotExistException;
import com.cluborg.authservice.security.jwt.JWTReactiveAuthenticationManager;
import com.cluborg.authservice.security.jwt.JWTToken;
import com.cluborg.authservice.security.jwt.TokenProvider;
import com.cluborg.authservice.user.domain.Authority;
import com.cluborg.authservice.user.domain.User;
import com.cluborg.authservice.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class LoginHandler {

    private final TokenProvider tokenProvider;
    private final JWTReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginHandler(TokenProvider tokenProvider, JWTReactiveAuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<ServerResponse> authorize(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(LoginVM.class)
                .map(authenticate)
                .flatMap(this.authenticationManager::authenticate)
                .doOnError(t -> new UserDoesNotExistException(HttpStatus.BAD_REQUEST, "Bad Request"))
                .map(auth -> {
                    ReactiveSecurityContextHolder.withAuthentication(auth);
                            String jwt = tokenProvider.createToken(auth);
                            return new JWTToken(jwt);
                        })
                .flatMap(
                        x -> ServerResponse.ok().syncBody(x)
                );
    }

    Function<LoginVM, Authentication> authenticate = t -> new UsernamePasswordAuthenticationToken(t.getUsername(), t.getPassword());

    public Mono<ServerResponse> addUser(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(AddUserDto.class)
                .map(t -> {
                    return new User(null, t.getUsername(), passwordEncoder.encode(t.getPassword()), getAuthority.apply(t.getAuthority()), true, true);
                })
                .flatMap(
                        u -> userRepository.save(u)
                ).flatMap(x -> ServerResponse.created(URI.create("/")).syncBody(x));
    }

    Function<String, Set<Authority>> getAuthority = s -> {
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(s));
        return authorities;
    };
}
