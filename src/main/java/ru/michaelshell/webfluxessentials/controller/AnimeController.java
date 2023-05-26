package ru.michaelshell.webfluxessentials.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.michaelshell.webfluxessentials.entity.Anime;
import ru.michaelshell.webfluxessentials.repository.AnimeRepository;

@RestController
@RequestMapping("/animes")
@RequiredArgsConstructor
public class AnimeController {

    private final AnimeRepository animeRepository;

    @GetMapping
    public Flux<Anime> findAll() {
        return animeRepository.findAll();
    }
}
