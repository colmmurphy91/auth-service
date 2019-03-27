package com.cluborg.authservice.security.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Component
public class LoginRestConfiguration {

    @Bean
    RouterFunction routerFunctions(LoginHandler loginHandler){
        return RouterFunctions
                .route(POST("/authorize"), loginHandler::authorize)
                .andRoute(POST("/addUser"), loginHandler::addUser);
    }
}
