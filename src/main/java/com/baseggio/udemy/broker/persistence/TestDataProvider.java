package com.baseggio.udemy.broker.persistence;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Adds test data to the database on startup
 */
@Slf4j
@Singleton
@Requires(notEnv = Environment.TEST)
public class TestDataProvider {

    private final SymbolsRepository symbolsRepository;
    private final QuotesRepository quotesRepository;
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public TestDataProvider(SymbolsRepository symbolsRepository, QuotesRepository quotesRepository) {
        this.symbolsRepository = symbolsRepository;
        this.quotesRepository = quotesRepository;
    }

    @EventListener
    public void init(StartupEvent event){
        if (symbolsRepository.findAll().isEmpty()) {
            log.info("Adding symbols test data.");
            Stream.of("AAPL", "AMZN", "FB", "TSLA")
                    .map(SymbolEntity::new)
                    .forEach(symbolsRepository::save);
        }
        if (quotesRepository.findAll().isEmpty()) {
            log.info("Adding quotes test data.");
            symbolsRepository.findAll().forEach(s -> {
                var quote = new QuoteEntity();
                quote.setAsk(randomNumber());
                quote.setBid(randomNumber());
                quote.setSymbol(s);
                quote.setLastPrice(randomNumber());
                quote.setVolume(randomNumber());
                quotesRepository.save(quote);
            });
        }
    }

    private BigDecimal randomNumber() {
        return BigDecimal.valueOf(RANDOM.nextDouble());
    }
}
