package io.vehiclestorage.controller;


import io.vehiclestorage.model.Vehicle;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StorageController {

    @PostMapping(value ="/store",
            consumes= MediaType.APPLICATION_JSON_VALUE)
    public List<Vehicle> storeVehicles(@RequestBody List<Vehicle> vehicles) {
        System.out.println("Storing vehicles: " + vehicles);
        return vehicles;
    }

}
