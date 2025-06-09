package org.example.restapiscrapper.common;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

@Component
public class SharedWriterImpl implements SharedWriter {
    public static String DEFAULT_FILENAME = "output";
    private final BufferedWriter writer;
    private final AppProperties appProperties;

    public SharedWriterImpl(AppProperties appProps) throws IOException {
        appProperties = appProps;
        if (appProps.getOutputFullName() == null) {
            writer = new BufferedWriter(
                    new FileWriter(DEFAULT_FILENAME + "." + appProperties.getOutputFormat(), true));
        }
        else {
            writer = new BufferedWriter(
                    new FileWriter(appProps.getOutputFullName(), true));
        }
    }

    public synchronized void write(JsonNode json) {
        try {
            if (appProperties.getOutputFormat().equalsIgnoreCase("json")) {
                writeToJson(json);
            }
            else if (appProperties.getOutputFormat().equalsIgnoreCase("csv")) {
                writeToCsv(json);
            }
            else {
                writer.write(json.toString());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToJson(JsonNode node) throws IOException {
        if (node.isArray()) {
            for (JsonNode item : node) {
                writer.write(item.toString());
                writer.newLine();
            }
        } else {
            writer.write(node.toString());
            writer.newLine();
        }
        writer.flush();
    }

    private void writeToCsv(JsonNode node) throws IOException {
        if (node.isArray()) {
            for (JsonNode obj : node) {
                writeCsvRow(obj);
                writer.write(",");
                writer.newLine();
            }
        } else {
            writeCsvRow(node);
            writer.newLine();
        }
        writer.flush();
    }

    private void writeCsvRow(JsonNode obj) throws IOException {
        boolean first = true;
        for (Iterator<String> it = obj.fieldNames(); it.hasNext(); ) {
            String field = it.next();
            JsonNode valueNode = obj.path(field);
            String value = escapeCsv(
                    valueNode.isValueNode() ? valueNode.asText("") : valueNode.toString()
            );
            if (!first) writer.write(",");
            writer.write(value);
            first = false;
        }
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    @PreDestroy
    public void close() throws IOException {
        writer.close();
    }
}