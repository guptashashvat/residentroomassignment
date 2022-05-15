package org.jhipster.facility.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.jhipster.facility.IntegrationTest;
import org.jhipster.facility.domain.Resident;
import org.jhipster.facility.domain.Room;
import org.jhipster.facility.repository.ResidentRepository;
import org.jhipster.facility.repository.search.ResidentSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ResidentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ResidentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PHONE_NUMBER = 9999999999999;
    private static final Integer UPDATED_PHONE_NUMBER = 9999999999998;

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/residents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/residents";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResidentRepository residentRepository;

    @Mock
    private ResidentRepository residentRepositoryMock;

    /**
     * This repository is mocked in the org.jhipster.facility.repository.search test package.
     *
     * @see org.jhipster.facility.repository.search.ResidentSearchRepositoryMockConfiguration
     */
    @Autowired
    private ResidentSearchRepository mockResidentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResidentMockMvc;

    private Resident resident;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resident createEntity(EntityManager em) {
        Resident resident = new Resident().name(DEFAULT_NAME).phone_number(DEFAULT_PHONE_NUMBER).email(DEFAULT_EMAIL);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        resident.setRoom(room);
        return resident;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resident createUpdatedEntity(EntityManager em) {
        Resident resident = new Resident().name(UPDATED_NAME).phone_number(UPDATED_PHONE_NUMBER).email(UPDATED_EMAIL);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createUpdatedEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        resident.setRoom(room);
        return resident;
    }

    @BeforeEach
    public void initTest() {
        resident = createEntity(em);
    }

    @Test
    @Transactional
    void createResident() throws Exception {
        int databaseSizeBeforeCreate = residentRepository.findAll().size();
        // Create the Resident
        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resident)))
            .andExpect(status().isCreated());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeCreate + 1);
        Resident testResident = residentList.get(residentList.size() - 1);
        assertThat(testResident.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testResident.getPhone_number()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testResident.getEmail()).isEqualTo(DEFAULT_EMAIL);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(1)).save(testResident);
    }

    @Test
    @Transactional
    void createResidentWithExistingId() throws Exception {
        // Create the Resident with an existing ID
        resident.setId(1L);

        int databaseSizeBeforeCreate = residentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resident)))
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeCreate);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(0)).save(resident);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setName(null);

        // Create the Resident, which fails.

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resident)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhone_numberIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setPhone_number(null);

        // Create the Resident, which fails.

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resident)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllResidents() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList
        restResidentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resident.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phone_number").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllResidentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(residentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restResidentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(residentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllResidentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(residentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restResidentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(residentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getResident() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get the resident
        restResidentMockMvc
            .perform(get(ENTITY_API_URL_ID, resident.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resident.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.phone_number").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getNonExistingResident() throws Exception {
        // Get the resident
        restResidentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewResident() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        int databaseSizeBeforeUpdate = residentRepository.findAll().size();

        // Update the resident
        Resident updatedResident = residentRepository.findById(resident.getId()).get();
        // Disconnect from session so that the updates on updatedResident are not directly saved in db
        em.detach(updatedResident);
        updatedResident.name(UPDATED_NAME).phone_number(UPDATED_PHONE_NUMBER).email(UPDATED_EMAIL);

        restResidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedResident.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedResident))
            )
            .andExpect(status().isOk());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
        Resident testResident = residentList.get(residentList.size() - 1);
        assertThat(testResident.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testResident.getPhone_number()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testResident.getEmail()).isEqualTo(UPDATED_EMAIL);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository).save(testResident);
    }

    @Test
    @Transactional
    void putNonExistingResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resident.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resident))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(0)).save(resident);
    }

    @Test
    @Transactional
    void putWithIdMismatchResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resident))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(0)).save(resident);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resident)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(0)).save(resident);
    }

    @Test
    @Transactional
    void partialUpdateResidentWithPatch() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        int databaseSizeBeforeUpdate = residentRepository.findAll().size();

        // Update the resident using partial update
        Resident partialUpdatedResident = new Resident();
        partialUpdatedResident.setId(resident.getId());

        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResident.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResident))
            )
            .andExpect(status().isOk());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
        Resident testResident = residentList.get(residentList.size() - 1);
        assertThat(testResident.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testResident.getPhone_number()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testResident.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void fullUpdateResidentWithPatch() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        int databaseSizeBeforeUpdate = residentRepository.findAll().size();

        // Update the resident using partial update
        Resident partialUpdatedResident = new Resident();
        partialUpdatedResident.setId(resident.getId());

        partialUpdatedResident.name(UPDATED_NAME).phone_number(UPDATED_PHONE_NUMBER).email(UPDATED_EMAIL);

        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResident.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResident))
            )
            .andExpect(status().isOk());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
        Resident testResident = residentList.get(residentList.size() - 1);
        assertThat(testResident.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testResident.getPhone_number()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testResident.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void patchNonExistingResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, resident.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resident))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(0)).save(resident);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resident))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(0)).save(resident);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(resident)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(0)).save(resident);
    }

    @Test
    @Transactional
    void deleteResident() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        int databaseSizeBeforeDelete = residentRepository.findAll().size();

        // Delete the resident
        restResidentMockMvc
            .perform(delete(ENTITY_API_URL_ID, resident.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Resident in Elasticsearch
        verify(mockResidentSearchRepository, times(1)).deleteById(resident.getId());
    }

    @Test
    @Transactional
    void searchResident() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        residentRepository.saveAndFlush(resident);
        when(mockResidentSearchRepository.search("id:" + resident.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(resident), PageRequest.of(0, 1), 1));

        // Search the resident
        restResidentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + resident.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resident.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phone_number").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }
}
