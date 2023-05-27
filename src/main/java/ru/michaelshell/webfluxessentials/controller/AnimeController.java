package ru.michaelshell.webfluxessentials.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.michaelshell.webfluxessentials.entity.Anime;
import ru.michaelshell.webfluxessentials.repository.AnimeRepository;
import ru.michaelshell.webfluxessentials.service.AnimeService;

@RestController
@RequestMapping("/animes")
@RequiredArgsConstructor
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping
    public Flux<Anime> findAll() {
        return animeService.findAll();
    }

    @GetMapping("{id}")
    public Mono<Anime> findById(@PathVariable Integer id) {
        return animeService.findById(id);
    }
}
