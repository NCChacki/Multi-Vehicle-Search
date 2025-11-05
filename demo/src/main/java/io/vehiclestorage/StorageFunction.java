package io.vehiclestorage;


import io.vehiclestorage.controller.StorageController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageFunction {

    @Bean
    public StorageController storageController() {
        return new StorageController();
    }
}
