package ru.michaelshell.webfluxessentials.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.michaelshell.webfluxessentials.entity.Anime;

public interface AnimeRepository extends R2dbcRepository<Anime, Integer> {
}
