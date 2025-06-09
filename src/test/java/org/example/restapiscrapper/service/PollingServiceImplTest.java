package org.example.restapiscrapper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restapiscrapper.common.AppProperties;
import org.example.restapiscrapper.common.SharedWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PollingServiceImplTest {

    private ThreadPoolExecutor executor;
    private ScheduledExecutorService scheduler;
    private SharedWriter writer;
    private WebClient webClient;
    private PollingServiceImpl service;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        executor   = mock(ThreadPoolExecutor.class);
        scheduler  = mock(ScheduledExecutorService.class);
        writer     = mock(SharedWriter.class);
        webClient  = mock(WebClient.class, RETURNS_DEEP_STUBS);
        AppProperties appProperties = new AppProperties();
        appProperties.setDelay(10);
        service = new PollingServiceImpl(executor, scheduler, writer, webClient, appProperties);
    }

    @Test
    void testStartPolling_executesAndSchedules() {
        doAnswer(inv -> {
            Runnable task = inv.getArgument(0);
            task.run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        JsonNode fakeJson = mapper.createObjectNode().put("message", "ok");
        when(webClient
                .get()
                .uri("https://example.com")
                .retrieve()
                .bodyToMono(JsonNode.class)
        ).thenReturn(Mono.just(fakeJson));

        service.startPolling("https://example.com");

        verify(writer).write(eq(fakeJson));

        verify(executor).execute(any(Runnable.class));

        verify(scheduler).schedule(
                Mockito.<Runnable>any(),
                eq(10L),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testShutdown() throws Exception {
        service.shutdown();
        verify(scheduler).shutdown();
        verify(executor).shutdown();
        verify(scheduler).awaitTermination(PollingServiceImpl.TERMINATION_AWAIT_TIME, TimeUnit.SECONDS);
        verify(executor).awaitTermination(PollingServiceImpl.TERMINATION_AWAIT_TIME, TimeUnit.SECONDS);
    }
}
