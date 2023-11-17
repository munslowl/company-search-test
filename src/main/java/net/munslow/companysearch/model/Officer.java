package net.munslow.companysearch.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Officer {
    String name;
    String officerRole;
    String appointedOn;
    Address address;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String resignedOn;

    @JsonIgnore
    public boolean isActive() {
        return resignedOn == null;
    }
}
