package ru.michaelshell.webfluxessentials.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAnimeDto(@NotBlank(message = "The name cannot be empty")
                             String name) {
}
