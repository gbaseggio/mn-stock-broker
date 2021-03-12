package com.baseggio.udemy.hello;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@MicronautTest
class HelloWorldControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testHelloResponse(){
        final String result = client.toBlocking().retrieve("hello");
        assertEquals("Hi", result);
    }

    @Test
    void testGreetInEnglish() {
        final String result = client.toBlocking().retrieve("hello/en");
        assertEquals("Hi", result);
    }

    @Test
    void testGreetInGerman() {
        final String result = client.toBlocking().retrieve("hello/de");
        assertEquals("Hallo", result);
    }

    @Test
    void testReturnsGreetingAsJson() {
        final ObjectNode result = client.toBlocking().retrieve("hello/json", ObjectNode.class);
        log.debug(result.toString());
    }

}
