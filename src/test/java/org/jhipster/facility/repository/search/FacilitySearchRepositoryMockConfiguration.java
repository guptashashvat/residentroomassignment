package org.jhipster.facility.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link FacilitySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class FacilitySearchRepositoryMockConfiguration {

    @MockBean
    private FacilitySearchRepository mockFacilitySearchRepository;
}
