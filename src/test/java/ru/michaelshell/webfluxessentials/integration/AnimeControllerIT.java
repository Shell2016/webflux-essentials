package ru.michaelshell.webfluxessentials.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.michaelshell.webfluxessentials.entity.Anime;
import ru.michaelshell.webfluxessentials.exception.GlobalErrorAttributes;
import ru.michaelshell.webfluxessentials.repository.AnimeRepository;
import ru.michaelshell.webfluxessentials.service.AnimeService;
import ru.michaelshell.webfluxessentials.util.AnimeCreator;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.when;


@WebFluxTest
@Import({AnimeService.class, WebProperties.Resources.class, GlobalErrorAttributes.class})
class AnimeControllerIT {

    private final Anime anime = AnimeCreator.createValidAnime();
    private final Anime animeToSave = AnimeCreator.createAnimeToSave();

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private AnimeRepository animeRepository;

//    @BeforeAll
//    static void blockHoundSetup() {
//        BlockHound.install();
//    }

    @BeforeEach
    void setUp() {
        when(animeRepository.findAll()).thenReturn(Flux.just(anime));
        when(animeRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));
        when(animeRepository.save(animeToSave)).thenReturn(Mono.just(anime));
        when(animeRepository.delete(ArgumentMatchers.any(Anime.class))).thenReturn(Mono.empty());
        when(animeRepository.saveAll(List.of(animeToSave, animeToSave))).thenReturn(Flux.just(anime, anime));
    }

    @Test
    @Disabled("blockhound tested")
    void blockhound() { //NOSONAR
        Mono.delay(Duration.ofMillis(1))
                .doOnNext(it -> {
                    try {
                        Thread.sleep(10);//NOSONAR
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block(); // should throw an exception about Thread.sleep
    }

    @Test
    void findAll() {
        webTestClient
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(anime.getId())
                .jsonPath("$.[0].name").isEqualTo(anime.getName());
    }

    @Test
    void findAllVariant2() {
        webTestClient
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Anime.class)
                .hasSize(1)
                .contains(anime);
    }

    @Test
    void findByIdReturnsMono() {
        webTestClient
                .get()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Anime.class)
                .isEqualTo(anime);
    }

    @Test
    void findByIdReturns404WhenNotExists() {
        when(animeRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

        webTestClient
                .get()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("Dev message example");
    }

    @Test
    void save() {
        webTestClient
                .post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(animeToSave))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Anime.class)
                .isEqualTo(anime);
    }

    @Test
    void save_ReturnsError_WhenNameIsEmpty() {
        Anime animeWithEmptyName = AnimeCreator.createAnimeToSave().withName("");

        webTestClient
                .post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(animeWithEmptyName))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void save_ReturnsError_WhenNameIsBlank() {
        Anime animeWithEmptyName = AnimeCreator.createAnimeToSave().withName(" ");

        webTestClient
                .post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(animeWithEmptyName))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void save_ReturnsError_WhenNameIsNull() {
        Anime animeWithEmptyName = new Anime();

        webTestClient
                .post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(animeWithEmptyName))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void batchSave() {
         webTestClient
                .post()
                .uri("/animes/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(List.of(animeToSave, animeToSave)))
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(Anime.class)
                .hasSize(2)
                .contains(anime);
    }

    @Test
    void batchSaveReturnsErrorWhenNameIsInvalid() {
        when(animeRepository.saveAll(ArgumentMatchers.anyIterable())).thenReturn(Flux.just(anime, anime.withName("")));

        webTestClient
                .post()
                .uri("/animes/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(List.of(animeToSave, animeToSave)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void delete() {
        webTestClient
                .delete()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteShouldReturnMonoErrorWhenEmptyMonoReturned() {
        when(animeRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("Dev message example");
    }

    @Test
    void updateShouldReturnEmptyMono() {
        when(animeRepository.save(anime)).thenReturn(Mono.just(anime));

        webTestClient
                .put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(anime))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateShouldReturnMonoErrorIfAnimeDoesNotExist() {
        when(animeRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

        webTestClient
                .put()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.developerMessage").isEqualTo("Dev message example");
    }
}
