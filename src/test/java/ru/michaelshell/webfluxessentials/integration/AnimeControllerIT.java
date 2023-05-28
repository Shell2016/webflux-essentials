package ru.michaelshell.webfluxessentials.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.michaelshell.webfluxessentials.entity.Anime;
import ru.michaelshell.webfluxessentials.repository.AnimeRepository;
import ru.michaelshell.webfluxessentials.service.AnimeService;
import ru.michaelshell.webfluxessentials.util.AnimeCreator;

import java.time.Duration;

import static org.mockito.Mockito.when;


@WebFluxTest
@Import({AnimeService.class, WebProperties.Resources.class})
class AnimeControllerIT {

    private final Anime anime = AnimeCreator.createValidAnime();

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
    }

    @Test
    @Disabled("blockhound tested")
    void blockhound() { //NOSONAR
        Mono.delay(Duration.ofMillis(1))
                .doOnNext(it -> {
                    try {
                        Thread.sleep(10);//NOSONAR
                    }
                    catch (InterruptedException e) {
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


}
