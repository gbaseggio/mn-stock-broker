package com.baseggio.udemy.auth.jwt;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
@Slf4j
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable HttpRequest<?> httpRequest,
            AuthenticationRequest<?, ?> authenticationRequest) {

        return Flowable.create(emitter -> {
            final Object identity = authenticationRequest.getIdentity();
            final Object secret = authenticationRequest.getSecret();
            log.debug("User {} tries to login...", identity);
            if (identity.equals("my-user") && secret.equals("secret")) {
                emitter.onNext(new UserDetails((String) identity, new ArrayList<>()));
                emitter.onComplete();
                return;
            }
            emitter.onError(new AuthenticationException(new AuthenticationFailed("Wrong username or password")));
        }, BackpressureStrategy.ERROR);


    }
}
