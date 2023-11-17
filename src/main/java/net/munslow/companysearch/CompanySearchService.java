package net.munslow.companysearch;

import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.munslow.companysearch.model.CompaniesSearchResult;
import net.munslow.companysearch.model.Officer;
import net.munslow.companysearch.model.SearchRequest;
import net.munslow.companysearch.truapi.TruClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanySearchService {
    private final TruClient truClient;
    private final CompanyCacheService companyCacheService;

    public CompaniesSearchResult search(@RequestBody SearchRequest request) {
        return Optional.ofNullable(request.getCompanyNumber())
                .map(this::searchByCompanyNumber)
                .orElseGet(() -> searchByQuery(request.getCompanyName()));
    }

    private CompaniesSearchResult searchByCompanyNumber(String companyNumber) {
        return companyCacheService
                .findById(companyNumber)
                .orElseGet(() -> searchByCompanyNumberAndCache(companyNumber));
    }

    private CompaniesSearchResult searchByCompanyNumberAndCache(String companyNumber) {
        log.info("Using API for company number {}", companyNumber);

        CompaniesSearchResult result = searchByQuery(companyNumber);

        companyCacheService.save(companyNumber, result);

        return result;
    }

    private CompaniesSearchResult searchByQuery(String query) {
        log.info("Using API for query {}", query);

        CompaniesSearchResult result = truClient.getCompanies(query);

        result
                .getItems()
                .forEach(c -> c.setOfficers(truClient.getOfficers(c.getCompanyNumber()).getItems()
                        .stream()
                        .filter(Officer::isActive)
                        .collect(Collectors.toList()))
                );

        return result;
    }
}
