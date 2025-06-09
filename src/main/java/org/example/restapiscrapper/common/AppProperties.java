package org.example.restapiscrapper.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @Min(1)
    private int maxPool;

    @Min(0)
    private int delay;

    @NotEmpty
    private List<String> names;

    @NotNull
    private String outputFormat;

    private String outputFullName;
}
