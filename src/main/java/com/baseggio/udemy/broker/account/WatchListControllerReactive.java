package com.baseggio.udemy.broker.account;

import com.baseggio.udemy.broker.model.InMemoryAccountStore;
import com.baseggio.udemy.broker.model.WatchList;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist-reactive")
@Slf4j
public class WatchListControllerReactive {

    public static final UUID ACCOUNT_ID = UUID.randomUUID();
    private final InMemoryAccountStore store;
    private final Scheduler scheduler;

    public WatchListControllerReactive(
            @Named(TaskExecutors.IO) ExecutorService executorService,
            InMemoryAccountStore store) {
        this.store = store;
        this.scheduler = Schedulers.from(executorService);
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList get() {
        log.info("getWatchList - {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_ID);
    }

    @Get(
            value = "/single",
            produces = MediaType.APPLICATION_JSON
    )
    public Single<WatchList> getAsSingle() {
        return Single.fromCallable(() -> {
           log.info("getAsSingle - {}", Thread.currentThread().getName());
           return store.getWatchList(ACCOUNT_ID);
        });
    }

    @Put(
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON
    )
    @ExecuteOn(TaskExecutors.IO)
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);

    }

    @Delete(
            value = "/{accountId}",
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON
    )
    @ExecuteOn(TaskExecutors.IO)
    public void delete(@PathVariable UUID accountId) {
        store.deleteWatchList(accountId);
    }
}
