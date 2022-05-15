package org.jhipster.facility.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jhipster.facility.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Room} entity.
 */
public interface RoomSearchRepository extends ElasticsearchRepository<Room, Long>, RoomSearchRepositoryInternal {}

interface RoomSearchRepositoryInternal {
    Page<Room> search(String query, Pageable pageable);
}

class RoomSearchRepositoryInternalImpl implements RoomSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    RoomSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Room> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Room> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Room.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
