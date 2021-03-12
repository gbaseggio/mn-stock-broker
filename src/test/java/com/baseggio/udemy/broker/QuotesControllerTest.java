package com.baseggio.udemy.broker;

import com.baseggio.udemy.broker.controller.QuotesController;
import com.baseggio.udemy.broker.model.CustomError;
import com.baseggio.udemy.broker.model.Quote;
import com.baseggio.udemy.broker.model.Symbol;
import com.baseggio.udemy.broker.store.InMemoryStore;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.micronaut.http.HttpRequest.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@Slf4j
class QuotesControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    InMemoryStore store;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void testReturnsQuotePerSymbol() {

        final Quote apple = initRandomQuote("APPL");

        store.update(apple);

        final Quote appleResult = client.toBlocking().retrieve(GET("/quotes/APPL"), Quote.class);
        log.debug("Result: {}", appleResult);
        assertThat(apple).isEqualToComparingFieldByField(appleResult);
    }

    @Test
    void returnsNotFoundOnUnsupportedSymbol(){
        try {
            client.toBlocking().retrieve(GET("/quotes/UNSUPPORTED"));
        } catch (HttpClientResponseException e) {

            final Optional<CustomError> notFound = e.getResponse().getBody(CustomError.class);
            assertTrue(notFound.isPresent());
            assertEquals(404, notFound.get().getStatus());

            assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());
        }
    }

    private Quote initRandomQuote(final String symbolValue) {
        return Quote.builder().symbol(new Symbol(symbolValue))
                .bid(randomValue())
                .ask(randomValue())
                .volume(randomValue())
                .lastPrice(randomValue())
                .build();
    }

    private BigDecimal randomValue(){
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1,100));
    }



}