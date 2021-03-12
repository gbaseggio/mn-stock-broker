package com.baseggio.udemy.hello.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class Greeting {
    private final String myText = "Hello World";
    private final BigDecimal id = BigDecimal.valueOf(123456789);
    private final Instant timeUTC = Instant.now();
}
