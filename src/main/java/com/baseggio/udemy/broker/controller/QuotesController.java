package com.baseggio.udemy.broker.controller;

import com.baseggio.udemy.broker.model.CustomError;
import com.baseggio.udemy.broker.model.Quote;
import com.baseggio.udemy.broker.persistence.QuoteDTO;
import com.baseggio.udemy.broker.persistence.QuoteEntity;
import com.baseggio.udemy.broker.persistence.QuotesRepository;
import com.baseggio.udemy.broker.persistence.SymbolEntity;
import com.baseggio.udemy.broker.store.InMemoryStore;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/quotes")
public class QuotesController {

    private final InMemoryStore store;
    private final QuotesRepository quotesRepository;

    public QuotesController(InMemoryStore store, QuotesRepository quotesRepository) {
        this.store = store;
        this.quotesRepository = quotesRepository;
    }

    @Operation(summary = "Returns a quote for the given symbol")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @ApiResponse(responseCode = "400", description = "Invalid Symbol specified")
    @Tag(name = "quotes")
    @Get("{symbol}")
    public HttpResponse getQuote(@PathVariable String symbol) {
        final Optional<Quote> maybeQuote = store.fetchQuote(symbol);
        if (maybeQuote.isEmpty()) {
            final CustomError notFound = CustomError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("Quote for symbol not available.")
                    .path("/quote/" + symbol)
                    .build();
            return HttpResponse.notFound(notFound);
        }
        return HttpResponse.ok(maybeQuote.get());
    }

    @Get("/jpa")
    public List<QuoteEntity> getAllQuotesViaJPA(){
        return quotesRepository.findAll();
    }

    @Operation(summary = "Returns a quote for the given symbol. Fetched via JPA.")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @ApiResponse(responseCode = "400", description = "Invalid Symbol specified")
    @Tag(name = "quotes")
    @Get("/jpa/{symbol}")
    public HttpResponse getQuoteViaJPA(@PathVariable String symbol) {
        final Optional<QuoteEntity> maybeQuote = quotesRepository.findBySymbol(new SymbolEntity(symbol));
        if (maybeQuote.isEmpty()) {
            final CustomError notFound = CustomError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("Quote for symbol not available in database.")
                    .path("/quote/jpa/" + symbol)
                    .build();
            return HttpResponse.notFound(notFound);
        }
        return HttpResponse.ok(maybeQuote.get());
    }

    @Get("/jpa/ordered/desc")
    public List<QuoteDTO> ordered() {
        return quotesRepository.listOrderByVolumeDesc();
    }

    @Get("/jpa/ordered/asc")
    public List<QuoteDTO> orderedasc() {
        return quotesRepository.listOrderByVolumeAsc();
    }

    @Get("/jpa/volume/{volume}")
    public List<QuoteDTO> filterByVolume(@PathVariable BigDecimal volume) {
        return quotesRepository.findByVolumeGreaterThanOrderByVolumeAsc(volume);
    }

    @Get("/jpa/pagination{?page,volume}")
    public List<QuoteDTO> volumeFilterPagination(@QueryValue Optional<Integer> page, @QueryValue Optional<BigDecimal> volume) {
        int tmpPage = page.isEmpty() ? 0 : page.get();
        BigDecimal tmpVolume = volume.isEmpty() ? BigDecimal.ZERO : volume.get();
        return quotesRepository.findByVolumeGreaterThan(tmpVolume, Pageable.from(tmpPage, 2));
    }

    @Get("/jpa/pagination/{page}")
    public Slice<QuoteDTO> allWithPagination(@PathVariable int page) {
        return quotesRepository.list(Pageable.from(page, 5));
    }
}
