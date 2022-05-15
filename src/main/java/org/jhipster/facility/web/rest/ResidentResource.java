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
import org.jhipster.facility.domain.Resident;
import org.jhipster.facility.repository.ResidentRepository;
import org.jhipster.facility.repository.search.ResidentSearchRepository;
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
 * REST controller for managing {@link org.jhipster.facility.domain.Resident}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ResidentResource {

    private final Logger log = LoggerFactory.getLogger(ResidentResource.class);

    private static final String ENTITY_NAME = "resident";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResidentRepository residentRepository;

    private final ResidentSearchRepository residentSearchRepository;

    public ResidentResource(ResidentRepository residentRepository, ResidentSearchRepository residentSearchRepository) {
        this.residentRepository = residentRepository;
        this.residentSearchRepository = residentSearchRepository;
    }

    /**
     * {@code POST  /residents} : Create a new resident.
     *
     * @param resident the resident to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resident, or with status {@code 400 (Bad Request)} if the resident has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/residents")
    public ResponseEntity<Resident> createResident(@Valid @RequestBody Resident resident) throws URISyntaxException {
        log.debug("REST request to save Resident : {}", resident);
        if (resident.getId() != null) {
            throw new BadRequestAlertException("A new resident cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Resident result = residentRepository.save(resident);
        residentSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/residents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /residents/:id} : Updates an existing resident.
     *
     * @param id the id of the resident to save.
     * @param resident the resident to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resident,
     * or with status {@code 400 (Bad Request)} if the resident is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resident couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/residents/{id}")
    public ResponseEntity<Resident> updateResident(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Resident resident
    ) throws URISyntaxException {
        log.debug("REST request to update Resident : {}, {}", id, resident);
        if (resident.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resident.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!residentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Resident result = residentRepository.save(resident);
        residentSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, resident.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /residents/:id} : Partial updates given fields of an existing resident, field will ignore if it is null
     *
     * @param id the id of the resident to save.
     * @param resident the resident to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resident,
     * or with status {@code 400 (Bad Request)} if the resident is not valid,
     * or with status {@code 404 (Not Found)} if the resident is not found,
     * or with status {@code 500 (Internal Server Error)} if the resident couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/residents/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Resident> partialUpdateResident(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Resident resident
    ) throws URISyntaxException {
        log.debug("REST request to partial update Resident partially : {}, {}", id, resident);
        if (resident.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resident.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!residentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Resident> result = residentRepository
            .findById(resident.getId())
            .map(existingResident -> {
                if (resident.getName() != null) {
                    existingResident.setName(resident.getName());
                }
                if (resident.getPhone_number() != null) {
                    existingResident.setPhone_number(resident.getPhone_number());
                }
                if (resident.getEmail() != null) {
                    existingResident.setEmail(resident.getEmail());
                }

                return existingResident;
            })
            .map(residentRepository::save)
            .map(savedResident -> {
                residentSearchRepository.save(savedResident);

                return savedResident;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, resident.getId().toString())
        );
    }

    /**
     * {@code GET  /residents} : get all the residents.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of residents in body.
     */
    @GetMapping("/residents")
    public ResponseEntity<List<Resident>> getAllResidents(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Residents");
        Page<Resident> page;
        if (eagerload) {
            page = residentRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = residentRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /residents} : get all the residents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of residents in body.
     */
    @GetMapping("/room/residents/{room_id}")
    public ResponseEntity<List<Resident>> getAllResidentsWithRoomId(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @PathVariable Long room_id
    ) {
        log.debug("REST request to get Resident with room id: {}", room_id);
        Page<Resident> page;
        page = residentRepository.findWithRoomId(pageable, room_id);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /residents/:id} : get the "id" resident.
     *
     * @param id the id of the resident to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resident, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/residents/{id}")
    public ResponseEntity<Resident> getResident(@PathVariable Long id) {
        log.debug("REST request to get Resident : {}", id);
        Optional<Resident> resident = residentRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(resident);
    }

    /**
     * {@code DELETE  /residents/:id} : delete the "id" resident.
     *
     * @param id the id of the resident to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/residents/{id}")
    public ResponseEntity<Void> deleteResident(@PathVariable Long id) {
        log.debug("REST request to delete Resident : {}", id);
        residentRepository.deleteById(id);
        residentSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/residents?query=:query} : search for the resident corresponding
     * to the query.
     *
     * @param query the query of the resident search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/residents")
    public ResponseEntity<List<Resident>> searchResidents(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Residents for query {}", query);
        Page<Resident> page = residentSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
