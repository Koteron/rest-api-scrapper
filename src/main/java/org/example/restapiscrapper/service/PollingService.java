package org.example.restapiscrapper.service;

import jakarta.annotation.PreDestroy;

public interface PollingService {
    void startPolling(String source);

    @PreDestroy
    void shutdown() throws Exception;
}
