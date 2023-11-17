package net.munslow.companysearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import lombok.SneakyThrows;
import net.munslow.companysearch.model.CachedCompaniesSearchResult;
import net.munslow.companysearch.model.CompaniesSearchResult;
import net.munslow.companysearch.model.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;

@ExtendWith(MockitoExtension.class)
class CompanyCacheServiceTest {
    @Mock
    CompanyCacheRepository companyCacheRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    CompanyCacheService companyCacheService;

    String cashedResultJson =
            """
                        {
                            "items": [
                                {
                                    "title": "ACME Co"
                                }
                            ]
                        }
                    """;

    @Captor
    ArgumentCaptor<CachedCompaniesSearchResult> captor;

    @BeforeEach
    void init() {
        companyCacheService = new CompanyCacheService(companyCacheRepository, objectMapper);
    }

    @Test
    void givenACompAnyNumberWhenSearchingForFirstTimeThenResultIsNotFromCache() {
        when(companyCacheRepository.findById("123456")).thenReturn(Optional.empty());

        assertThat(companyCacheService.findById("123456")).isEmpty();
    }

    @Test
    void givenACompAnyNumberWhenSearchingForSecondTimeThenResultIsFromCache() {
        CachedCompaniesSearchResult result = CachedCompaniesSearchResult.of("123456", cashedResultJson);

        when(companyCacheRepository.findById("123456")).thenReturn(Optional.of(result));

        assertThat(companyCacheService.findById("123456").get().getItems())
                .extracting(Company::getTitle)
                .containsExactly("ACME Co");
    }

    @Test
    @SneakyThrows
    void givenASearchResultWhenSaveIsCalledThenWillSaveToDatabase() {
        var company = Company.builder().title("ACME Co").build();

        var searchResult = CompaniesSearchResult.of(List.of(company));

        companyCacheService.save("222222", searchResult);

        verify(companyCacheRepository).save(captor.capture());

        var cashedSearchResult = captor.getValue();

        assertThat(cashedSearchResult.getCompanyNumber()).isEqualTo("222222");

        JSONAssert.assertEquals(cashedResultJson, cashedSearchResult.getJson(), false);
    }
}
