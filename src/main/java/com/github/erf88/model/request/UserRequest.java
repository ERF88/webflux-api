package com.github.erf88.model.request;

public record UserRequest(
        String name,
        String email,
        String password
) {}
