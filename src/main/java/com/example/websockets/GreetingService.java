package com.example.websockets;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@Service
public class GreetingService {

    /**
     * Private method that returns the formatted greeting.
     * @param name
     * @return
     */
    private GreetingResponse greet (String name){
        return new GreetingResponse("Hello " + name + " @ " + Instant.now());
    }

    /**
     * Returns zero or one formatted greeting.
     *
     * @param request
     * @return
     */
    public Mono<GreetingResponse> greet (GreetingRequest request){
        return Mono.just(greet(request.getName()));
    }

    /**
     * Returns zero or more formatted greetings at one second intervals indefinitely
     *
     * @param request
     * @return
     */
    public Flux<GreetingResponse> greets(GreetingRequest request) {

        return Flux
                .fromStream(Stream.generate(() -> greet(request.getName())))
                .delayElements(Duration.ofSeconds(1));
    }
}