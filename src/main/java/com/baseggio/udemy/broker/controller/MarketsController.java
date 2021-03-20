package com.baseggio.udemy.broker.controller;

import com.baseggio.udemy.broker.model.Symbol;
import com.baseggio.udemy.broker.persistence.SymbolEntity;
import com.baseggio.udemy.broker.persistence.SymbolsRepository;
import com.baseggio.udemy.broker.store.InMemoryStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;


@Controller("/markets")
public class MarketsController {

    private final InMemoryStore store;
    private final SymbolsRepository repository;

    public MarketsController(InMemoryStore store, SymbolsRepository repository) {
        this.store = store;
        this.repository = repository;
    }

    @Operation(summary = "Return all available markets")
    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Tag(name = "markets")
    @Get("/")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public List<Symbol> all() {
        return store.getAllSymbols();
    }

    @Operation(summary = "Return all available markets via jpa")
    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Tag(name = "markets")
    @Get("/jpa")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public Single<List<SymbolEntity>> allSymbolsViaJPA(){
        return Single.just(repository.findAll());
    }

}
