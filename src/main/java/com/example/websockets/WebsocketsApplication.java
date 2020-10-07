package com.example.websockets;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.Map;

@Log4j2
@SpringBootApplication
public class WebsocketsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketsApplication.class, args);
    }

    @Configuration
    class GreetingWebSocketConfiguration {

        @Bean
        SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler webSocketHandler){
            return new SimpleUrlHandlerMapping(Map.of("/ws/greetings", webSocketHandler ), 10);
        }

        @Bean
        WebSocketHandler webSocketHandler(GreetingService greetingService) {
            return (webSocketSession) -> {

                Flux<WebSocketMessage> webSocketMessageFlux = webSocketSession
                        .receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .map(GreetingRequest::new)
                        .flatMap(greetingService::greets)
                        .map(GreetingResponse::getMessage)
                        .map(webSocketSession::textMessage)
                        .doOnEach(signal -> log.info(signal.get().toString()))
                        .doFinally(signalType -> log.info("finally: " + signalType.toString()));


                return webSocketSession.send(webSocketMessageFlux);
            };
        }

        @Bean
        WebSocketHandlerAdapter webSocketHandlerAdapter() {
            return new WebSocketHandlerAdapter();
        }
    }
}
