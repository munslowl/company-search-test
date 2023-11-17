package net.munslow.companysearch.model;

import lombok.Data;

@Data
public class Address {
    String locality;
    String postalCode;
    String premises;
    String addressLine1;
    String country;
}
