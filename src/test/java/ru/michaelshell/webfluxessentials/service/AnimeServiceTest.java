package ru.michaelshell.webfluxessentials.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.michaelshell.webfluxessentials.entity.Anime;
import ru.michaelshell.webfluxessentials.repository.AnimeRepository;
import ru.michaelshell.webfluxessentials.util.AnimeCreator;

import java.time.Duration;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @Mock
    private AnimeRepository animeRepositoryMock;

    @InjectMocks
    private AnimeService animeService;

    private final Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    static void blockHoundSetup() {
        BlockHound.install();
    }

    @Test
    @Disabled("blockhound tested")
    void blockhound() {
        Mono.delay(Duration.ofMillis(1))
                .doOnNext(it -> {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block(); // should throw an exception about Thread.sleep
    }

    @Test
    @DisplayName("findAll return a flux of anime")
    void findAllReturnFluxWhenSuccessful() {
        when(animeRepositoryMock.findAll()).thenReturn(Flux.just(anime));

        StepVerifier.create(animeService.findAll())
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById return Mono of anime")
    void findByIdReturnsMono() {
        when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));

        StepVerifier.create(animeService.findById(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById return Mono error when empty mono returned")
    void findByIdReturnsMonoErrorWhenEmptyMonoReturned() {
        when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(animeService.findById(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("save returns mono of anime")
    void saveReturnsMono() {
        Anime animeToSave = AnimeCreator.createAnimeToSave();
        when(animeRepositoryMock.save(animeToSave)).thenReturn(Mono.just(anime));

        StepVerifier.create(animeService.save(animeToSave))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    void delete() {
        when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class))).thenReturn(Mono.empty());
        when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));

        StepVerifier.create(animeService.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void deleteShouldReturnMonoErrorWhenEmptyMonoReturned() {
        when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(animeService.delete(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void updateShouldReturnEmptyMono() {
        when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));
        when(animeRepositoryMock.save(anime)).thenReturn(Mono.just(anime));

        StepVerifier.create(animeService.update(anime))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void updateShouldReturnMonoErrorIfAnimeDoesNotExist() {
        when(animeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(animeService.update(anime))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();

    }


}