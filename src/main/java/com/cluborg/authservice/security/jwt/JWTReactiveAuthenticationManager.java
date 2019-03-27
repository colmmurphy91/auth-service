package com.cluborg.authservice.security.jwt;

import com.cluborg.authservice.security.exception.BadCredentialsException;
import com.cluborg.authservice.security.exception.UserDoesNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author duc-d
 */
public class JWTReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public JWTReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }
        return Mono.just(authentication)
                .switchIfEmpty(Mono.defer(this::raiseBadCredentials))
                .cast(UsernamePasswordAuthenticationToken.class)
                .flatMap(this::authenticateToken)
                .publishOn(Schedulers.parallel())
                .onErrorResume(e -> raiseNoUserFoundException())
                .filter(u -> passwordEncoder.matches((String) authentication.getCredentials(), u.getPassword()))
                .switchIfEmpty(Mono.defer(this::raiseBadCredentials))
                .map(u -> new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), u.getAuthorities()));
    }

    private <T> Mono<T> raiseNoUserFoundException() {
        return Mono.error(new UserDoesNotExistException(HttpStatus.BAD_REQUEST, "User Does Not Exist"));
    }

    private <T> Mono<T> raiseBadCredentials() {
        return Mono.error(new BadCredentialsException(HttpStatus.UNAUTHORIZED, "Password Incorrect"));
    }

    private Mono<UserDetails> authenticateToken(final UsernamePasswordAuthenticationToken authenticationToken) {
        String username = authenticationToken.getName();

        logger.info("checking authentication for user " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("authenticated user " + username + ", setting security context");
            return this.userDetailsService.findByUsername(username);
        }

        return null;
    }
}
