package org.jhipster.facility.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jhipster.facility.domain.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Facility} entity.
 */
public interface FacilitySearchRepository extends ElasticsearchRepository<Facility, Long>, FacilitySearchRepositoryInternal {}

interface FacilitySearchRepositoryInternal {
    Page<Facility> search(String query, Pageable pageable);
}

class FacilitySearchRepositoryInternalImpl implements FacilitySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    FacilitySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Facility> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Facility> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Facility.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
