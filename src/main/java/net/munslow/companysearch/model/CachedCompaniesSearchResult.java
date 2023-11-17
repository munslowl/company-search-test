package net.munslow.companysearch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CachedCompaniesSearchResult {
    @Id
    String companyNumber;

    @Lob
    @Column(columnDefinition = "text")
    String json;
}
