package com.atipera.gr.handler;

import com.atipera.gr.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Test
    void test5xxServerErrorHandling() {
        ClientResponse mockResponse = mock(ClientResponse.class);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(mockResponse.bodyToMono(String.class)).thenReturn(Mono.just("Internal Server Error"));

        ExchangeFilterFunction errorHandler = GlobalExceptionHandler.webClientErrorHandler();

        ClientRequest mockRequest = mock(ClientRequest.class);

        ExchangeFunction mockExchangeFunction = clientRequest -> Mono.just(mockResponse);

        Mono<ClientResponse> responseMono = errorHandler.filter(mockRequest, mockExchangeFunction);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof ApplicationException &&
                                ((ApplicationException) throwable).getStatus() == HttpStatus.INTERNAL_SERVER_ERROR &&
                                throwable.getMessage().equals("Internal Server Error")
                )
                .verify();
    }

    @Test
    void test4xxClientErrorHandling() {
        ClientResponse mockResponse = mock(ClientResponse.class);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.bodyToMono(String.class)).thenReturn(Mono.just("Bad Request"));

        ExchangeFilterFunction errorHandler = GlobalExceptionHandler.webClientErrorHandler();

        ClientRequest mockRequest = mock(ClientRequest.class);

        ExchangeFunction mockExchangeFunction = clientRequest -> Mono.just(mockResponse);

        Mono<ClientResponse> responseMono = errorHandler.filter(mockRequest, mockExchangeFunction);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof ApplicationException &&
                                ((ApplicationException) throwable).getStatus() == HttpStatus.BAD_REQUEST &&
                                throwable.getMessage().equals("Bad Request")
                )
                .verify();
    }

    @Test
    void testSuccessResponseHandling() {
        ClientResponse mockResponse = mock(ClientResponse.class);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK);

        ExchangeFilterFunction errorHandler = GlobalExceptionHandler.webClientErrorHandler();

        ClientRequest mockRequest = mock(ClientRequest.class);

        ExchangeFunction mockExchangeFunction = clientRequest -> Mono.just(mockResponse);

        Mono<ClientResponse> responseMono = errorHandler.filter(mockRequest, mockExchangeFunction);

        StepVerifier.create(responseMono)
                .expectNext(mockResponse)
                .verifyComplete();
    }
}
