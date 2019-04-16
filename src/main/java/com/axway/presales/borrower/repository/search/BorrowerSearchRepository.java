package com.axway.presales.borrower.repository.search;

import com.axway.presales.borrower.domain.Borrower;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Borrower entity.
 */
public interface BorrowerSearchRepository extends ElasticsearchRepository<Borrower, Long> {
}
