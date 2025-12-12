package com.example;

public record Account(
        int userId,
        String name,
        String password,
        String firstName,
        String lastName,
        String ssn
) {}