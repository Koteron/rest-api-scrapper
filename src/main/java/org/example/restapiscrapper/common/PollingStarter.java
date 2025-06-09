package org.example.restapiscrapper.common;

import lombok.RequiredArgsConstructor;
import org.example.restapiscrapper.service.PollingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PollingStarter implements CommandLineRunner {
    @Value("classpath:urls")
    private Resource sourceFileResource;
    private final PollingService pollingService;
    private final AppProperties appProperties;

    @Override
    public void run(String... args) throws Exception {
        List<String> sources = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(sourceFileResource.getInputStream()))) {
            while (reader.ready()) {
                String[] record = reader.readLine().split(" ");
                if (appProperties.getNames().contains(record[0])) {
                    sources.add(record[1]);
                }
            }
        }
        if (sources.isEmpty()) {
            System.out.println("Polling could not start: no sources found for given names");
        }
        else {
            System.out.println("Polling from: " + sources);
            for (String source : sources) {
                pollingService.startPolling(source);
            }
        }
    }
}
