package io.vehiclestorage.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    int length;
    int quantity;
    int width= 5;
}
