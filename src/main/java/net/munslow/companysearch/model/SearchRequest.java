package net.munslow.companysearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    @JsonProperty("companyName")
    String companyName;

    @JsonProperty("companyNumber")
    String companyNumber;
}
