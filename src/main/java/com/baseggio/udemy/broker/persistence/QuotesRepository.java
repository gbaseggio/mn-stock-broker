package com.baseggio.udemy.broker.persistence;

import com.baseggio.udemy.broker.model.Quote;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotesRepository extends CrudRepository<QuoteEntity, String> {
    @NonNull
    @Override
    List<QuoteEntity> findAll();

    Optional<QuoteEntity> findBySymbol(SymbolEntity symbol);

    // Ordering
    List<QuoteDTO> listOrderByVolumeDesc();

    List<QuoteDTO> listOrderByVolumeAsc();

    // Filter & Ordering
    List<QuoteDTO> findByVolumeGreaterThanOrderByVolumeAsc(BigDecimal volume);

    //Pagination
    List<QuoteDTO> findByVolumeGreaterThan(BigDecimal volume, Pageable pageable);
    Slice<QuoteDTO> list(Pageable pageable);

}
