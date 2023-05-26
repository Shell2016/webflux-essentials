package ru.michaelshell.webfluxessentials.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@Table("anime")
public class Anime {

    @Id
    Integer id;

    @NotEmpty(message = "The name cannot be empty")
    String name;
}
