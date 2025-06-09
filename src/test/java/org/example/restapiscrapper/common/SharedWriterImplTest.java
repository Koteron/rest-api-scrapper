package org.example.restapiscrapper.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SharedWriterImplTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @TempDir
    Path tempDir;

    private List<String> writeAndRead(String format, String jsonStr) throws IOException {
        Path outFile = tempDir.resolve(SharedWriterImpl.DEFAULT_FILENAME + "." + format);

        AppProperties appProps = new AppProperties();
        appProps.setOutputFormat(format);
        appProps.setOutputFullName(outFile.toFile().getAbsolutePath());
        SharedWriterImpl writer = new SharedWriterImpl(appProps);

        JsonNode input = MAPPER.readTree(jsonStr);

        writer.write(input);
        writer.close();

        return Files.readAllLines(outFile, StandardCharsets.UTF_8);
    }

    @Test
    void testWriteCsv() throws IOException {
        List<String> lines = writeAndRead("csv",
                "{ \"id\": 1, \"name\": \"dog\", \"info\": { \"age\": 3 } }");

        assertEquals(1, lines.size());
        String line = lines.getFirst();
        assertTrue(line.startsWith("1,dog,"));
        assertTrue(line.contains("{\"\"age\"\":3}"));
    }

    @Test
    void testWriteCsvArray() throws IOException {
        List<String> lines = writeAndRead("csv",
                "[ {\"id\":1}, {\"id\":2} ]");

        assertEquals(2, lines.size());
        assertEquals("1,", lines.get(0));
        assertEquals("2,", lines.get(1));
    }

    @Test
    void testWriteJsonArray() throws IOException {
        List<String> lines = writeAndRead("json",
                "[ {\"id\":1}, {\"id\":2} ]");

        assertEquals(2, lines.size());
        assertEquals("{\"id\":1}", lines.get(0));
        assertEquals("{\"id\":2}", lines.get(1));
    }

    @Test
    void testWriteJson() throws IOException {
        List<String> lines = writeAndRead("json",
                "{\"id\":1}");

        assertEquals(1, lines.size());
        assertEquals("{\"id\":1}", lines.getFirst());
    }

    @Test
    void testWriteOther() throws IOException {
        List<String> lines = writeAndRead("txt",
                "[{\"id\":1},{\"id\":2}]");

        assertEquals(1, lines.size());
        System.out.println(lines.getFirst());
        assertEquals("[{\"id\":1},{\"id\":2}]", lines.getFirst());
    }
}