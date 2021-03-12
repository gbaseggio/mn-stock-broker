package com.baseggio.udemy.broker.store;

import com.baseggio.udemy.broker.model.Quote;
import com.baseggio.udemy.broker.model.Symbol;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class InMemoryStore {

    private final List<Symbol> symbols;
    private final Map<String, Quote> cachedQuotes = new HashMap<>();
    private final ThreadLocalRandom current = ThreadLocalRandom.current();

    public InMemoryStore() {
        symbols = Stream
            .of("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA")
            .map(Symbol::new)
            .collect(Collectors.toList());
        symbols.forEach(symbol -> {
            cachedQuotes.put(symbol.getValue(), randomQuote(symbol));
        });
    }

    private Quote randomQuote(Symbol symbol) {
        return Quote.builder().symbol(symbol)
                .bid(randomValue())
                .ask(randomValue())
                .volume(randomValue())
                .lastPrice(randomValue())
                .build();
    }

    public List<Symbol> getAllSymbols() {
        return symbols;
    }

    public Optional<Quote> fetchQuote(final String symbol) {
        return Optional.ofNullable(cachedQuotes.get(symbol));
    }

    private BigDecimal randomValue(){
        return BigDecimal.valueOf(current.nextDouble(1,100));
    }

    public void update(final Quote quote) {
        cachedQuotes.put(quote.getSymbol().getValue(), quote);
    }
}
