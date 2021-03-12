package com.baseggio.udemy.broker.account;

import com.baseggio.udemy.broker.model.*;
import com.baseggio.udemy.broker.store.InMemoryStore;
import com.baseggio.udemy.broker.account.WatchListController;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.*;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Slf4j
class WatchListControllerTest {

    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    InMemoryAccountStore store;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void testReturnsEmptyWatchListForAccount(){

        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        var request = GET("/account/watchlist")
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final WatchList watchList = client.toBlocking().retrieve(request, WatchList.class);
        assertTrue(watchList.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnWatchListForAccount(){

        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        final List<Symbol> symbolList = Stream.of("APPL", "AMZN", "NFLX").map(Symbol::new).collect(Collectors.toList());
        WatchList watchList = new WatchList(symbolList);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        var request = GET("/account/watchlist")
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void canUpdateListForAccount(){

        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        final List<Symbol> symbolList = Stream.of("APPL", "AMZN", "NFLX").map(Symbol::new).collect(Collectors.toList());
        WatchList watchList = new WatchList(symbolList);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        var request = PUT("/account/watchlist", watchList)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final HttpResponse<Object> added = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
    }

    @Test
    void canDeleteWathcListFromAccount(){

        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        final List<Symbol> symbolList = Stream.of("APPL", "AMZN", "NFLX").map(Symbol::new).collect(Collectors.toList());
        WatchList watchList = new WatchList(symbolList);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

        var request = DELETE("/account/watchlist/" + TEST_ACCOUNT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final HttpResponse<Object> deleted = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, deleted.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    private BearerAccessRefreshToken givenMyUserIsLoggedIn() {
        final UsernamePasswordCredentials upc = new UsernamePasswordCredentials("my-user", "secret");
        var login = HttpRequest.POST("/login", upc);
        var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        log.debug("Login bearer token: {} - expires in {}", response.body().getAccessToken(), response.body().getExpiresIn());

        assertEquals(HttpStatus.OK, response.getStatus());
        final BearerAccessRefreshToken token = response.body();
        assertNotNull(token);
        assertEquals("my-user", token.getUsername());
        return token;
    }
}