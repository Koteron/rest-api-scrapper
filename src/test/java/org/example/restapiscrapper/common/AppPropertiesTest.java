package org.example.restapiscrapper.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AppPropertiesTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(AppConfig.class);

    static Stream<TestCase> validCases() {
        return Stream.of(
                new TestCase("Valid minimal", List.of(
                        "app.maxPool=1",
                        "app.delay=0",
                        "app.names=a",
                        "app.outputFormat=json"
                ), true),
                new TestCase("Valid multiple names", List.of(
                        "app.maxPool=10",
                        "app.delay=5",
                        "app.names=one,two,three",
                        "app.outputFormat=csv",
                        "app.outputFullName=result.csv"
                ), true)
        );
    }

    static Stream<TestCase> invalidCases() {
        return Stream.of(
                new TestCase("Missing required", List.of(
                        "app.delay=1",
                        "app.outputFormat=csv"
                ), false),
                new TestCase("Empty name list", List.of(
                        "app.maxPool=2",
                        "app.delay=1",
                        "app.names=",
                        "app.outputFormat=json"
                ), false),
                new TestCase("Invalid int input (symbol in int)", List.of(
                        "app.maxPool=p",
                        "app.delay=0",
                        "app.names=name",
                        "app.outputFormat=json"
                ), false),
                new TestCase("Negative delay", List.of(
                        "app.maxPool=3",
                        "app.delay=-1",
                        "app.names=name",
                        "app.outputFormat=json"
                ), false)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validCases")
    void shouldStartSuccessfullyWithValidConfigs(TestCase testCase) {
        contextRunner.withPropertyValues(testCase.properties.toArray(new String[0]))
                .run(context -> assertThat(context).hasNotFailed());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCases")
    void shouldFailWithInvalidConfigs(TestCase testCase) {
        contextRunner.withPropertyValues(testCase.properties.toArray(new String[0]))
                .run(context -> assertThat(context).hasFailed());
    }

    private record TestCase(String name, List<String> properties, boolean shouldStart) {
        @Override
        public String toString() {
            return name;
        }
    }
}
