package org.example.restapiscrapper.common;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PreDestroy;

import java.io.IOException;


public interface SharedWriter {
    void write(JsonNode json);

    @PreDestroy
    void close() throws IOException;
}