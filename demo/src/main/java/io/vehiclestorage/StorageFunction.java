package io.vehiclestorage;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;

@Configuration
public class StorageFunction {

    @Bean
    public Function<List<String>, List<String>> storagePlanner() {
        return vehicles -> vehicles.stream()
                .map(v -> "planned:" + v)
                .toList();
    }
}
