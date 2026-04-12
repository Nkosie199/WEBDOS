package com.webdocs.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenBankService {

    private final WebClient openBankClient;

    public OpenBankService(@Qualifier("openBankClient") WebClient openBankClient) {
        this.openBankClient = openBankClient;
    }

    public Mono<Object> getAccountsByUsername(String username) {
        return openBankClient.get()
                .uri("/api/account/" + username)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorReturn(java.util.List.of());
    }

    public Mono<Object> getTransactionsByUsername(String username) {
        return openBankClient.get()
                .uri("/api/transactions/user/" + username)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorReturn(java.util.List.of());
    }
}
