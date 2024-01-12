package com.github.erf88.model.response;

public record UserResponse(
        String id,
        String name,
        String email,
        String password
) {}
