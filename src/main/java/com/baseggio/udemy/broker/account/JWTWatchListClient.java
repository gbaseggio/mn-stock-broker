package com.baseggio.udemy.broker.account;

import com.baseggio.udemy.broker.model.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.util.UUID;

@Client("/")
public interface JWTWatchListClient {

    @Post("/login")
    BearerAccessRefreshToken login(@Body UsernamePasswordCredentials credentials);

    @Get("/account/watchlist-reactive")
    Flowable<WatchList> retrieveWatchList(@Header String authorization);

    @Get("/account/watchlist-reactive/single")
    Single<WatchList> retrieveWatchListAsSingle(@Header String authorization);

    @Put("/account/watchlist-reactive")
    HttpResponse<WatchList> updateWatchList(@Header String authorization, @Body WatchList watchList);

    @Delete("/account/watchlist-reactive/{testAccountId}")
    HttpResponse<Object> deleteWathList(@Header String authorization, @PathVariable UUID testAccountId);
}
