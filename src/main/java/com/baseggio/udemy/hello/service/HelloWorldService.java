package com.baseggio.udemy.hello.service;

import com.baseggio.udemy.hello.configuration.GreetingConfig;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class HelloWorldService {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldService.class);
    private final GreetingConfig config;

    @EventListener
    private void onStartUp(StartupEvent startupEvent) {
        LOG.info("Startup: {}", HelloWorldService.class.getSimpleName());
    }

    public HelloWorldService(GreetingConfig config) {
        this.config = config;
    }

    public String sayHi() {
        return config.getEn();
    }

}
