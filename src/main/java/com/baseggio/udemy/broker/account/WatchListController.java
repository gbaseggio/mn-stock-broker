package com.baseggio.udemy.broker.account;

import com.baseggio.udemy.broker.model.InMemoryAccountStore;
import com.baseggio.udemy.broker.model.WatchList;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist")
public class WatchListController {

    public static final UUID ACCOUNT_ID = UUID.randomUUID();
    private final InMemoryAccountStore store;

    public WatchListController(InMemoryAccountStore store) {
        this.store = store;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {
        return store.getWatchList(ACCOUNT_ID);
    }

    @Put(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON
    )
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);

    }

    @Delete(
            value = "/{accountId}",
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON
    )
    public void delete(@PathVariable UUID accountId) {
        store.deleteWatchList(accountId);
    }
}
