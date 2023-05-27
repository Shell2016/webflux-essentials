package ru.michaelshell.webfluxessentials.dto;

import jakarta.validation.constraints.NotBlank;

public record AnimeDto(@NotBlank(message = "The name cannot be empty")
                             String name) {
}
