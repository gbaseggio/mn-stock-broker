package com.baseggio.udemy.broker.account;

import com.baseggio.udemy.broker.model.InMemoryAccountStore;
import com.baseggio.udemy.broker.model.Symbol;
import com.baseggio.udemy.broker.model.WatchList;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
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
class WatchListControllerReactiveTest {

    private static final UUID TEST_ACCOUNT_ID = WatchListControllerReactive.ACCOUNT_ID;
    private static final String ACCOUNT_WATCHLIST = "/account/watchlist";

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    InMemoryAccountStore store;

    @Inject
    @Client("/")
    JWTWatchListClient client;

    @Test
    void testReturnsEmptyWatchListForAccount(){
        final Single<WatchList> result = client.retrieveWatchList(getAuthorization()).singleOrError();
        assertTrue(result.blockingGet().getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnWatchListForAccount(){

        final List<Symbol> symbolList = Stream.of("APPL", "AMZN", "NFLX").map(Symbol::new).collect(Collectors.toList());
        WatchList watchList = new WatchList(symbolList);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        final WatchList result = client.retrieveWatchList(getAuthorization()).singleOrError().blockingGet();
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void returnWatchListForAccountAsSingle(){

        final List<Symbol> symbolList = Stream.of("APPL", "AMZN", "NFLX").map(Symbol::new).collect(Collectors.toList());
        WatchList watchList = new WatchList(symbolList);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        final Single<WatchList> result = client.retrieveWatchListAsSingle(getAuthorization());
        assertEquals(3, result.blockingGet().getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void canUpdateListForAccount(){

        final List<Symbol> symbolList = Stream.of("APPL", "AMZN", "NFLX").map(Symbol::new).collect(Collectors.toList());
        WatchList watchList = new WatchList(symbolList);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        final HttpResponse<WatchList> added = client.updateWatchList(getAuthorization(), watchList);
        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
    }

    @Test
    void canDeleteWathcListFromAccount(){

        final List<Symbol> symbolList = Stream.of("APPL", "AMZN", "NFLX").map(Symbol::new).collect(Collectors.toList());
        WatchList watchList = new WatchList(symbolList);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

        final HttpResponse<Object> deleted = client.deleteWathList(getAuthorization(), WatchListControllerReactive.ACCOUNT_ID);
        assertEquals(HttpStatus.OK, deleted.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    private String getAuthorization() {
        return "Bearer " + givenMyUserLoggedIn().getAccessToken();
    }

    private BearerAccessRefreshToken givenMyUserLoggedIn() {
        return client.login(new UsernamePasswordCredentials("my-user", "secret"));
    }

}