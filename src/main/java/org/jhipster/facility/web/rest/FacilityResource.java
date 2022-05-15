package org.jhipster.facility.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.jhipster.facility.domain.Facility;
import org.jhipster.facility.repository.FacilityRepository;
import org.jhipster.facility.repository.search.FacilitySearchRepository;
import org.jhipster.facility.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.jhipster.facility.domain.Facility}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FacilityResource {

    private final Logger log = LoggerFactory.getLogger(FacilityResource.class);

    private static final String ENTITY_NAME = "facility";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FacilityRepository facilityRepository;

    private final FacilitySearchRepository facilitySearchRepository;

    public FacilityResource(FacilityRepository facilityRepository, FacilitySearchRepository facilitySearchRepository) {
        this.facilityRepository = facilityRepository;
        this.facilitySearchRepository = facilitySearchRepository;
    }

    /**
     * {@code POST  /facilities} : Create a new facility.
     *
     * @param facility the facility to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new facility, or with status {@code 400 (Bad Request)} if the facility has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/facilities")
    public ResponseEntity<Facility> createFacility(@Valid @RequestBody Facility facility) throws URISyntaxException {
        log.debug("REST request to save Facility : {}", facility);
        if (facility.getId() != null) {
            throw new BadRequestAlertException("A new facility cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Facility result = facilityRepository.save(facility);
        facilitySearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/facilities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /facilities/:id} : Updates an existing facility.
     *
     * @param id the id of the facility to save.
     * @param facility the facility to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facility,
     * or with status {@code 400 (Bad Request)} if the facility is not valid,
     * or with status {@code 500 (Internal Server Error)} if the facility couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/facilities/{id}")
    public ResponseEntity<Facility> updateFacility(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Facility facility
    ) throws URISyntaxException {
        log.debug("REST request to update Facility : {}, {}", id, facility);
        if (facility.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, facility.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Facility result = facilityRepository.save(facility);
        facilitySearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, facility.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /facilities/:id} : Partial updates given fields of an existing facility, field will ignore if it is null
     *
     * @param id the id of the facility to save.
     * @param facility the facility to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facility,
     * or with status {@code 400 (Bad Request)} if the facility is not valid,
     * or with status {@code 404 (Not Found)} if the facility is not found,
     * or with status {@code 500 (Internal Server Error)} if the facility couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/facilities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Facility> partialUpdateFacility(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Facility facility
    ) throws URISyntaxException {
        log.debug("REST request to partial update Facility partially : {}, {}", id, facility);
        if (facility.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, facility.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Facility> result = facilityRepository
            .findById(facility.getId())
            .map(existingFacility -> {
                if (facility.getFacility_name() != null) {
                    existingFacility.setFacility_name(facility.getFacility_name());
                }

                return existingFacility;
            })
            .map(facilityRepository::save)
            .map(savedFacility -> {
                facilitySearchRepository.save(savedFacility);

                return savedFacility;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, facility.getId().toString())
        );
    }

    /**
     * {@code GET  /facilities} : get all the facilities.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of facilities in body.
     */
    @GetMapping("/facilities")
    public ResponseEntity<List<Facility>> getAllFacilities(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Facilities");
        Page<Facility> page = facilityRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /facilities/:id} : get the "id" facility.
     *
     * @param id the id of the facility to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the facility, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/facilities/{id}")
    public ResponseEntity<Facility> getFacility(@PathVariable Long id) {
        log.debug("REST request to get Facility : {}", id);
        Optional<Facility> facility = facilityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(facility);
    }

    /**
     * {@code DELETE  /facilities/:id} : delete the "id" facility.
     *
     * @param id the id of the facility to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/facilities/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Long id) {
        log.debug("REST request to delete Facility : {}", id);
        facilityRepository.deleteById(id);
        facilitySearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/facilities?query=:query} : search for the facility corresponding
     * to the query.
     *
     * @param query the query of the facility search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/facilities")
    public ResponseEntity<List<Facility>> searchFacilities(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Facilities for query {}", query);
        Page<Facility> page = facilitySearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
