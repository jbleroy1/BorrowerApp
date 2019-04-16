package com.axway.presales.borrower.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of CreditSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CreditSearchRepositoryMockConfiguration {

    @MockBean
    private CreditSearchRepository mockCreditSearchRepository;

}
