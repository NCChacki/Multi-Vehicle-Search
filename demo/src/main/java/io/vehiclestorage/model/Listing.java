package io.vehiclestorage.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
public class Listing {
    private String id;
    private String location_id;
    int length;
    int width;
    int price_in_cents;
}
