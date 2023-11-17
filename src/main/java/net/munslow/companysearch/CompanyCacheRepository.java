package net.munslow.companysearch;

import net.munslow.companysearch.model.CachedCompaniesSearchResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyCacheRepository extends CrudRepository<CachedCompaniesSearchResult, String> {
}
