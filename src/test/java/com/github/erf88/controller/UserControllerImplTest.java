package com.github.erf88.controller;

import com.github.erf88.entity.User;
import com.github.erf88.mapper.UserMapper;
import com.github.erf88.model.request.UserRequest;
import com.github.erf88.model.response.UserResponse;
import com.github.erf88.service.UserService;
import com.github.erf88.service.exception.ObjectNotFoundException;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    public static final String ID = "123";
    public static final String BASE_URI = "/users";
    public static final String NAME = "usuario";
    public static final String EMAIL = "usuario@email.com";
    public static final String PASSWORD = "usuario123";

    @DisplayName("Test save endpoint with success")
    @Test
    void testSaveWithSuccess() {
        final UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);
        when(service.save(any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated();

        verify(service, times(1)).save(any(UserRequest.class));
    }

    @DisplayName("Test save endpoint with bad request")
    @Test
    void testSaveWithBadRequest() {
        expectValidation(new UserRequest(NAME.concat(" "), EMAIL, PASSWORD))
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("field cannot have black spaces at the beginning or at end");

        expectValidation(new UserRequest("u", EMAIL, PASSWORD))
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("must be between 3 and 50 characters");

        expectValidation(new UserRequest(null, EMAIL, PASSWORD))
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("must not be null or empty");

        expectValidation(new UserRequest(NAME, EMAIL.replace("@", ""), PASSWORD))
                .jsonPath("$.errors[0].fieldName").isEqualTo("email")
                .jsonPath("$.errors[0].message").isEqualTo("invalid email");
    }

    private WebTestClient.BodyContentSpec expectValidation(UserRequest request) {
        return webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo(BASE_URI)
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes");
    }

    @DisplayName("Test find by id endpoint with success")
    @Test
    void testFindByIdWithSuccess() {
        final UserResponse userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
        when(service.findById(anyString())).thenReturn(Mono.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get()
                .uri(BASE_URI.concat("/").concat(ID))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(service, times(1)).findById(anyString());
        verify(mapper, times(1)).toResponse(any(User.class));
    }

    @DisplayName("Test find by id endpoint with not found")
    @Test
    void testFindByIdWithNotFound() {
        final String message = "Object not found. Id: %s, Type: %s".formatted(ID, User.class.getSimpleName());
        final String uri = BASE_URI.concat("/").concat(ID);
        when(service.findById(anyString())).thenThrow(new ObjectNotFoundException(message));

        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.status").isEqualTo(NOT_FOUND.value())
                .jsonPath("$.error").isEqualTo(NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo(message);

        verify(service, times(1)).findById(anyString());
    }

    @DisplayName("Test find all endpoint with success")
    @Test
    void testFindAllWithSuccess() {
        final UserResponse userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
        when(service.findAll()).thenReturn(Flux.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(ID)
                .jsonPath("$.[0].name").isEqualTo(NAME)
                .jsonPath("$.[0].email").isEqualTo(EMAIL)
                .jsonPath("$.[0].password").isEqualTo(PASSWORD);

        verify(service, times(1)).findAll();
        verify(mapper, times(1)).toResponse(any(User.class));
    }

    @DisplayName("Test update endpoint with success")
    @Test
    void testUpdateWithSuccess() {
        final UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);
        final UserResponse userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
        when(service.update(anyString(), any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.patch()
                .uri(BASE_URI.concat("/").concat(ID))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(service, times(1)).update(anyString(), any(UserRequest.class));
        verify(mapper, times(1)).toResponse(any(User.class));
    }

    @DisplayName("Test delete endpoint with success")
    @Test
    void testDeleteWithSuccess() {
        when(service.delete(anyString())).thenReturn(Mono.just(User.builder().build()));

        webTestClient.delete()
                .uri(BASE_URI.concat("/").concat(ID))
                .exchange()
                .expectStatus().isOk();

        verify(service, times(1)).delete(anyString());
    }

}