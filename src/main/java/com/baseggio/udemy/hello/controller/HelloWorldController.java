package com.baseggio.udemy.hello.controller;

import com.baseggio.udemy.hello.configuration.GreetingConfig;
import com.baseggio.udemy.hello.model.Greeting;
import com.baseggio.udemy.hello.service.HelloWorldService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("${controller.paths.hello:/hello}")
@Slf4j
public class HelloWorldController {

    private final HelloWorldService service;
    private final GreetingConfig config;

    public HelloWorldController(HelloWorldService service, GreetingConfig config) {
        this.service = service;
        this.config = config;
    }

    @Get("/")
    public String index(){
        log.info("index() called");
        return service.sayHi();
    }

    @Get("/en")
    public String greetInEnglish() {
        log.info("greetInEnglish() called");
        return this.config.getEn();
    }

    @Get("/de")
    public String greetInGerman() {
        log.info("greetInGerman() called");
        return this.config.getDe();
    }

    @Get("/json")
    public Greeting getGreeting() {
        return new Greeting();
    }

}
