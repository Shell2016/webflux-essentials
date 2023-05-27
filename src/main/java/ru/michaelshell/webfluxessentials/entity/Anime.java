package ru.michaelshell.webfluxessentials.entity;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "The name cannot be empty")
    String name;
}
