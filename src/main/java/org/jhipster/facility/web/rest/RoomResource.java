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
import org.jhipster.facility.domain.Room;
import org.jhipster.facility.repository.RoomRepository;
import org.jhipster.facility.repository.search.RoomSearchRepository;
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
 * REST controller for managing {@link org.jhipster.facility.domain.Room}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RoomResource {

    private final Logger log = LoggerFactory.getLogger(RoomResource.class);

    private static final String ENTITY_NAME = "room";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoomRepository roomRepository;

    private final RoomSearchRepository roomSearchRepository;

    public RoomResource(RoomRepository roomRepository, RoomSearchRepository roomSearchRepository) {
        this.roomRepository = roomRepository;
        this.roomSearchRepository = roomSearchRepository;
    }

    /**
     * {@code POST  /rooms} : Create a new room.
     *
     * @param room the room to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new room, or with status {@code 400 (Bad Request)} if the room has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rooms")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) throws URISyntaxException {
        log.debug("REST request to save Room : {}", room);
        if (room.getId() != null) {
            throw new BadRequestAlertException("A new room cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Room result = roomRepository.save(room);
        roomSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/rooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rooms/:id} : Updates an existing room.
     *
     * @param id the id of the room to save.
     * @param room the room to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated room,
     * or with status {@code 400 (Bad Request)} if the room is not valid,
     * or with status {@code 500 (Internal Server Error)} if the room couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rooms/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Room room)
        throws URISyntaxException {
        log.debug("REST request to update Room : {}, {}", id, room);
        if (room.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, room.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Room result = roomRepository.save(room);
        roomSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, room.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /rooms/:id} : Partial updates given fields of an existing room, field will ignore if it is null
     *
     * @param id the id of the room to save.
     * @param room the room to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated room,
     * or with status {@code 400 (Bad Request)} if the room is not valid,
     * or with status {@code 404 (Not Found)} if the room is not found,
     * or with status {@code 500 (Internal Server Error)} if the room couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rooms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Room> partialUpdateRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Room room
    ) throws URISyntaxException {
        log.debug("REST request to partial update Room partially : {}, {}", id, room);
        if (room.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, room.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Room> result = roomRepository
            .findById(room.getId())
            .map(existingRoom -> {
                if (room.getRoom_number() != null) {
                    existingRoom.setRoom_number(room.getRoom_number());
                }

                return existingRoom;
            })
            .map(roomRepository::save)
            .map(savedRoom -> {
                roomSearchRepository.save(savedRoom);

                return savedRoom;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, room.getId().toString())
        );
    }

    /**
     * {@code GET  /rooms} : get all the rooms.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rooms in body.
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getAllRooms(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Rooms");
        Page<Room> page;
        if (eagerload) {
            page = roomRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = roomRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rooms} : get all the rooms.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rooms in body.
     */
    @GetMapping("/facility/rooms/{facility_id}")
    public ResponseEntity<List<Room>> getAllResidentsWithRoomId(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @PathVariable Long facility_id
    ) {
        log.debug("REST request to get Room with facility id: {}", facility_id);
        Page<Room> page;
        page = roomRepository.findWithFacilityId(pageable, facility_id);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rooms/:id} : get the "id" room.
     *
     * @param id the id of the room to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the room, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rooms/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        log.debug("REST request to get Room : {}", id);
        Optional<Room> room = roomRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(room);
    }

    /**
     * {@code DELETE  /rooms/:id} : delete the "id" room.
     *
     * @param id the id of the room to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        log.debug("REST request to delete Room : {}", id);
        roomRepository.deleteById(id);
        roomSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/rooms?query=:query} : search for the room corresponding
     * to the query.
     *
     * @param query the query of the room search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/rooms")
    public ResponseEntity<List<Room>> searchRooms(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Rooms for query {}", query);
        Page<Room> page = roomSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
