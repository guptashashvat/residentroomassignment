package org.jhipster.facility.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jhipster.facility.domain.Resident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Resident} entity.
 */
public interface ResidentSearchRepository extends ElasticsearchRepository<Resident, Long>, ResidentSearchRepositoryInternal {}

interface ResidentSearchRepositoryInternal {
    Page<Resident> search(String query, Pageable pageable);
}

class ResidentSearchRepositoryInternalImpl implements ResidentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ResidentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Resident> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Resident> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Resident.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
