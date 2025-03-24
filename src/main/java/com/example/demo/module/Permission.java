package com.example.demo.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    TRAINER_READ("trainer:read"),
    TRAINER_UPDATE("trainer:update"),
    TRAINER_CREATE("trainer:create"),

    USER_READ("user:read"),
    USER_UPDATE("user:update");

    @Getter
    private final String permission;
}
