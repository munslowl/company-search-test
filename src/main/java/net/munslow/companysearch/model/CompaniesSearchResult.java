package net.munslow.companysearch.model;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CompaniesSearchResult {

    List<Company> items = Collections.emptyList();

    public int getTotalResults() {
        return items.size();
    }
}
