package com.axway.presales.borrower.repository.search;

import com.axway.presales.borrower.domain.Credit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Credit entity.
 */
public interface CreditSearchRepository extends ElasticsearchRepository<Credit, Long> {
}
