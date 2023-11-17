package net.munslow.companysearch.truapi;

import net.munslow.companysearch.model.CompaniesSearchResult;
import net.munslow.companysearch.model.OfficersSearchResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "truClient", url = "${tru.url}")
public interface TruClient {
    @GetMapping("/Search")
    CompaniesSearchResult getCompanies(@RequestParam("Query") String query);

    @GetMapping("/Officers")
    OfficersSearchResult getOfficers(@RequestParam("CompanyNumber") String companyNumber);
}
