package ru.michaelshell.webfluxessentials.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.michaelshell.webfluxessentials.entity.Anime;
import ru.michaelshell.webfluxessentials.service.AnimeService;
import ru.michaelshell.webfluxessentials.util.AnimeCreator;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @Mock
    private AnimeService animeService;

    @InjectMocks
    private AnimeController animeController;

    private final Anime anime = AnimeCreator.createValidAnime();

//    @BeforeAll
//    static void blockHoundSetup() {
//        BlockHound.install();
//    }

    @Test
    void findAllReturnFluxWhenSuccessful() {
        when(animeService.findAll()).thenReturn(Flux.just(anime));

        StepVerifier.create(animeController.findAll())
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById return Mono of anime")
    void findByIdReturnsMono() {
        when(animeService.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));

        StepVerifier.create(animeController.findById(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("save returns mono of anime")
    void saveReturnsMono() {
        Anime animeToSave = AnimeCreator.createAnimeToSave();
        when(animeService.save(animeToSave)).thenReturn(Mono.just(anime));

        StepVerifier.create(animeController.save(animeToSave))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    void batchSaveReturnFluxOfAnime() {
        Anime animeToSave = AnimeCreator.createAnimeToSave();
        when(animeService.saveAll(List.of(animeToSave, animeToSave))).thenReturn(Flux.just(anime, anime));

        StepVerifier.create(animeController.batchSave(List.of(animeToSave, animeToSave)))
                .expectSubscription()
                .expectNext(anime, anime)
                .verifyComplete();
    }

    @Test
    void delete() {
        when(animeService.delete(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(animeController.delete(1))
                .expectSubscription()
                .verifyComplete();
    }


    @Test
    void updateShouldReturnEmptyMono() {
        when(animeService.update(anime)).thenReturn(Mono.empty());

        StepVerifier.create(animeController.update(anime, 1))
                .expectSubscription()
                .verifyComplete();
    }


}