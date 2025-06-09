package org.example.restapiscrapper.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.example.restapiscrapper.common.AppProperties;
import org.example.restapiscrapper.common.SharedWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PollingServiceImpl implements PollingService {
    public static final int TERMINATION_AWAIT_TIME = 10;

    private final ThreadPoolExecutor executor;
    private final ScheduledExecutorService scheduler;
    private final SharedWriter writer;
    private final WebClient webClient;
    private final AppProperties appProperties;

    public void startPolling(String source) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Polling " + source + " in " + Thread.currentThread().getName());
                    writer.write(
                            webClient.get()
                            .uri(source)
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .block());
                    System.out.println("Result from " + source + " written in " + Thread.currentThread().getName());
                } catch (Exception e) {
                    System.err.println("Error polling " + source);
                } finally {
                    scheduler.schedule(() -> executor.execute(this), appProperties.getDelay(), TimeUnit.SECONDS);
                }
            }
        };

        executor.execute(task);
    }

    @PreDestroy
    public void shutdown(){
        System.out.println("Shutting down...");

        scheduler.shutdown();
        executor.shutdown();

        try {
            if (!scheduler.awaitTermination(TERMINATION_AWAIT_TIME, TimeUnit.SECONDS)) {
                System.err.println("Scheduler did not terminate in time. Forcing shutdown");
                scheduler.shutdownNow();
            }

            if (!executor.awaitTermination(TERMINATION_AWAIT_TIME, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in time. Forcing shutdown");
                executor.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            System.err.println("Shutdown interrupted. Forcing immediate shutdown");
            scheduler.shutdownNow();
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Shutdown complete.");
    }
}
