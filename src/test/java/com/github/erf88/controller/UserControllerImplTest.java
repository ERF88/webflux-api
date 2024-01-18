package com.github.erf88.controller;

import com.github.erf88.entity.User;
import com.github.erf88.mapper.UserMapper;
import com.github.erf88.model.request.UserRequest;
import com.github.erf88.service.UserService;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerImplTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService service;

    @MockBean
    private UserMapper mapper;

    @MockBean
    private MongoClient mongoClient;

    @DisplayName("Test endpoint save with success")
    @Test
    void testSaveWithSuccess() {
        final UserRequest request = new UserRequest("usuario", "usuario@email.com", "usuario123");
        when(service.save(any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus()
                .isCreated();

        verify(service, times(1)).save(any(UserRequest.class));
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

}