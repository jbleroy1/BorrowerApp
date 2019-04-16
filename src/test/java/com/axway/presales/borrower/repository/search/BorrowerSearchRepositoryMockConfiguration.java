package com.axway.presales.borrower.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of BorrowerSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class BorrowerSearchRepositoryMockConfiguration {

    @MockBean
    private BorrowerSearchRepository mockBorrowerSearchRepository;

}
