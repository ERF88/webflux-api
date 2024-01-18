package com.github.erf88.service;

import com.github.erf88.entity.User;
import com.github.erf88.mapper.UserMapper;
import com.github.erf88.model.request.UserRequest;
import com.github.erf88.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testSave() {
        UserRequest request = new UserRequest("usuario", "usuario@email.com", "usuario123");
        User entity = User.builder().build();

        when(mapper.toEntity(any(UserRequest.class))).thenReturn(entity);
        when(repository.save(any(User.class))).thenReturn(Mono.just(User.builder().build()));

        Mono<User> result = userService.save(request);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testFindById() {
        when(repository.findById(anyString())).thenReturn(Mono.just(User.builder().build()));

        Mono<User> result = userService.findById("123");

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Flux.just(User.builder().build()));

        Flux<User> result = userService.findAll();

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        verify(repository, times(1)).findAll();
    }

    @Test
    void testUpdate() {
        UserRequest request = new UserRequest("usuario", "usuario@email.com", "usuario123");
        User entity = User.builder().build();

        when(mapper.toEntity(any(UserRequest.class), any(User.class))).thenReturn(entity);
        when(repository.findById(anyString())).thenReturn(Mono.just(entity));
        when(repository.save(any(User.class))).thenReturn(Mono.just(entity));

        Mono<User> result = userService.update("123", request);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        verify(repository, times(1)).save(any(User.class));
    }

}