package com.baseggio.udemy.hello.configuration;

import io.micronaut.context.annotation.ConfigurationInject;
import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

/**
 * Immutable configuration object.
 */
@ConfigurationProperties("hello.config.greeting")
public class GreetingConfig {

    @Getter
    private final String de;
    @Getter
    private final String en;

    @ConfigurationInject
    public GreetingConfig(@NotBlank String de, @NotBlank String en) {
        this.de = de;
        this.en = en;
    }

}
