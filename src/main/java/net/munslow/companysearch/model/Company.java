package net.munslow.companysearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    String companyNumber;
    String companyType;
    String title;
    String companyStatus;
    String dateOfCreation;
    Address address;
    @Builder.Default
    List<Officer> officers = Collections.emptyList();
}
