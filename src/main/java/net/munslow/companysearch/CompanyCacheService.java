package net.munslow.companysearch;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.munslow.companysearch.model.CachedCompaniesSearchResult;
import net.munslow.companysearch.model.CompaniesSearchResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyCacheService {
    private final CompanyCacheRepository companyCacheRepository;
    private final ObjectMapper objectMapper;

    public Optional<CompaniesSearchResult> findById(String companyNumber) {
        log.info("Checking cache for company number {}", companyNumber);

        return Optional.ofNullable(companyNumber)
                .flatMap(
                        number ->
                                companyCacheRepository
                                        .findById(companyNumber)
                                        .map(CachedCompaniesSearchResult::getJson)
                                        .map(this::jsonToResult));
    }

    @SneakyThrows
    public void save(String companyNumber, CompaniesSearchResult result) {
        String json = objectMapper.writeValueAsString(result);
        companyCacheRepository.save(CachedCompaniesSearchResult.of(companyNumber, json));
    }

    @SneakyThrows
    private CompaniesSearchResult jsonToResult(String json) {
        return objectMapper.readValue(json, CompaniesSearchResult.class);
    }
}
