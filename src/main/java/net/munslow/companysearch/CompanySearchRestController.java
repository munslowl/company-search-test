package net.munslow.companysearch;

import lombok.RequiredArgsConstructor;
import net.munslow.companysearch.model.CompaniesSearchResult;
import net.munslow.companysearch.model.SearchRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CompanySearchRestController {
    private final CompanySearchService companySearchService;

    @PostMapping
    public CompaniesSearchResult search(@RequestBody SearchRequest request) {
        return companySearchService.search(request);
    }
}
