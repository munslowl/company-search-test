package net.munslow.companysearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import net.munslow.companysearch.model.*;
import net.munslow.companysearch.truapi.TruClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanySearchServiceTest {
    @Mock
    TruClient truClient;
    @Mock
    CompanyCacheService companyCacheService;

    CompanySearchService companySearchService;

    Officer activeOfficer1 = Officer.builder().name("John Smith").build();
    Officer activeOfficer2 = Officer.builder().name("Sally James").build();
    Officer inactiveOfficer1 = Officer.builder().name("Frank Jones").resignedOn("2023-01-01").build();
    Company company1 = Company.builder().companyNumber("222222").title("Acme co").build();
    Company company2 = Company.builder().companyNumber("333333").title("Acme Inc").build();

    @BeforeEach
    void init() {
        companySearchService = new CompanySearchService(truClient, companyCacheService);
    }

    @Test
    void givenACompanyNumberWhenSearchIsCalledThenAPIRequestIsMadeUsingCompanyNumber() {
        var companyNumber = "222222";

        when(companyCacheService.findById(companyNumber)).thenReturn(Optional.empty());

        CompaniesSearchResult companiesSearchResult = CompaniesSearchResult.of(List.of(company1));

        when(truClient.getCompanies(companyNumber)).thenReturn(companiesSearchResult);

        when(truClient.getOfficers(companyNumber))
                .thenReturn(OfficersSearchResult.of(List.of(activeOfficer1, activeOfficer2)));

        SearchRequest request = SearchRequest.builder().companyNumber(companyNumber).build();

        var searchResult = companySearchService.search(request);

        assertThat(searchResult.getItems())
                .extracting(Company::getTitle)
                .containsExactly(company1.getTitle());

        assertThat(searchResult.getItems().get(0))
                .extracting(Company::getOfficers)
                .asList()
                .containsExactly(activeOfficer1, activeOfficer2);

        verify(companyCacheService).findById(companyNumber);
        verify(companyCacheService).save(eq(companyNumber), eq(companiesSearchResult));
    }

    @Test
    void givenACompanyNameWhenSearchIsCalledThenAPIRequestIsMadeUsingCompanyName() {
        var companyName = "Acme";

        CompaniesSearchResult companiesSearchResult =
                CompaniesSearchResult.of(List.of(company1, company2));

        when(truClient.getCompanies(companyName)).thenReturn(companiesSearchResult);

        when(truClient.getOfficers("222222"))
                .thenReturn(OfficersSearchResult.of(List.of(activeOfficer1)));
        when(truClient.getOfficers("333333"))
                .thenReturn(OfficersSearchResult.of(List.of(activeOfficer1, activeOfficer2)));

        SearchRequest request = SearchRequest.builder().companyName(companyName).build();

        var searchResult = companySearchService.search(request);

        assertThat(searchResult.getItems())
                .extracting(Company::getTitle)
                .containsExactly(company1.getTitle(), company2.getTitle());

        assertThat(searchResult.getItems().get(0))
                .extracting(Company::getOfficers)
                .asList()
                .containsExactly(activeOfficer1);

        assertThat(searchResult.getItems().get(1))
                .extracting(Company::getOfficers)
                .asList()
                .containsExactly(activeOfficer1, activeOfficer2);

        verifyNoInteractions(companyCacheService);
    }

    @Test
    void givenACompanyNumberAndACompanyNameWhenSearchIsCalledThenAPIRequestIsMadeUsingCompanyNumber() {
        var companyNumber = "222222";

        when(companyCacheService.findById(companyNumber)).thenReturn(Optional.empty());

        CompaniesSearchResult companiesSearchResult = CompaniesSearchResult.of(List.of(company1));

        when(truClient.getCompanies(companyNumber)).thenReturn(companiesSearchResult);

        when(truClient.getOfficers(companyNumber))
                .thenReturn(OfficersSearchResult.of(List.of(activeOfficer1, activeOfficer2)));

        SearchRequest request =
                SearchRequest.builder().companyNumber(companyNumber).companyName("Acme").build();

        var searchResult = companySearchService.search(request);

        assertThat(searchResult.getItems())
                .extracting(Company::getTitle)
                .containsExactly(company1.getTitle());

        assertThat(searchResult.getItems().get(0))
                .extracting(Company::getOfficers)
                .asList()
                .containsExactly(activeOfficer1, activeOfficer2);

        verify(companyCacheService).findById(companyNumber);
        verify(companyCacheService).save(eq(companyNumber), eq(companiesSearchResult));
    }

    @Test
    void givenASearchCriteriaWhenSearchIsCalledAndOfficeIsResignedThenResultDoesNotContainResignedOfficer() {
        var companyNumber = "222222";

        when(companyCacheService.findById(companyNumber)).thenReturn(Optional.empty());

        CompaniesSearchResult companiesSearchResult = CompaniesSearchResult.of(List.of(company1));

        when(truClient.getCompanies(companyNumber)).thenReturn(companiesSearchResult);

        when(truClient.getOfficers(companyNumber))
                .thenReturn(OfficersSearchResult.of(List.of(activeOfficer1, inactiveOfficer1)));

        SearchRequest request = SearchRequest.builder().companyNumber(companyNumber).build();

        var searchResult = companySearchService.search(request);

        assertThat(searchResult.getItems())
                .extracting(Company::getTitle)
                .containsExactly(company1.getTitle());

        assertThat(searchResult.getItems().get(0))
                .extracting(Company::getOfficers)
                .asList()
                .containsExactly(activeOfficer1);

        verify(companyCacheService).findById(companyNumber);
        verify(companyCacheService).save(eq(companyNumber), eq(companiesSearchResult));
    }

    @Test
    void givenACompanyNumberWhenSearchIsCalledAndResultIsCachedThenResultIsFromCache() {
        var companyNumber = "222222";

        CompaniesSearchResult companiesSearchResult = CompaniesSearchResult.of(List.of(company1));

        when(companyCacheService.findById(companyNumber))
                .thenReturn(Optional.of(companiesSearchResult));

        SearchRequest request = SearchRequest.builder().companyNumber(companyNumber).build();

        var searchResult = companySearchService.search(request);

        assertThat(searchResult.getItems())
                .extracting(Company::getTitle)
                .containsExactly(company1.getTitle());

        verify(companyCacheService).findById(companyNumber);
        verifyNoMoreInteractions(companyCacheService);
        verifyNoInteractions(truClient);
    }
}
