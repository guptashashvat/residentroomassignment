package org.jhipster.facility.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.jhipster.facility.IntegrationTest;
import org.jhipster.facility.domain.Facility;
import org.jhipster.facility.repository.FacilityRepository;
import org.jhipster.facility.repository.search.FacilitySearchRepository;
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
 * Integration tests for the {@link FacilityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FacilityResourceIT {

    private static final String DEFAULT_FACILITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FACILITY_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/facilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/facilities";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FacilityRepository facilityRepository;

    /**
     * This repository is mocked in the org.jhipster.facility.repository.search test package.
     *
     * @see org.jhipster.facility.repository.search.FacilitySearchRepositoryMockConfiguration
     */
    @Autowired
    private FacilitySearchRepository mockFacilitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacilityMockMvc;

    private Facility facility;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Facility createEntity(EntityManager em) {
        Facility facility = new Facility().facility_name(DEFAULT_FACILITY_NAME);
        return facility;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Facility createUpdatedEntity(EntityManager em) {
        Facility facility = new Facility().facility_name(UPDATED_FACILITY_NAME);
        return facility;
    }

    @BeforeEach
    public void initTest() {
        facility = createEntity(em);
    }

    @Test
    @Transactional
    void createFacility() throws Exception {
        int databaseSizeBeforeCreate = facilityRepository.findAll().size();
        // Create the Facility
        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facility)))
            .andExpect(status().isCreated());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeCreate + 1);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getFacility_name()).isEqualTo(DEFAULT_FACILITY_NAME);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(1)).save(testFacility);
    }

    @Test
    @Transactional
    void createFacilityWithExistingId() throws Exception {
        // Create the Facility with an existing ID
        facility.setId(1L);

        int databaseSizeBeforeCreate = facilityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facility)))
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeCreate);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(0)).save(facility);
    }

    @Test
    @Transactional
    void checkFacility_nameIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityRepository.findAll().size();
        // set the field null
        facility.setFacility_name(null);

        // Create the Facility, which fails.

        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facility)))
            .andExpect(status().isBadRequest());

        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFacilities() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        // Get all the facilityList
        restFacilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facility.getId().intValue())))
            .andExpect(jsonPath("$.[*].facility_name").value(hasItem(DEFAULT_FACILITY_NAME)));
    }

    @Test
    @Transactional
    void getFacility() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        // Get the facility
        restFacilityMockMvc
            .perform(get(ENTITY_API_URL_ID, facility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(facility.getId().intValue()))
            .andExpect(jsonPath("$.facility_name").value(DEFAULT_FACILITY_NAME));
    }

    @Test
    @Transactional
    void getNonExistingFacility() throws Exception {
        // Get the facility
        restFacilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFacility() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();

        // Update the facility
        Facility updatedFacility = facilityRepository.findById(facility.getId()).get();
        // Disconnect from session so that the updates on updatedFacility are not directly saved in db
        em.detach(updatedFacility);
        updatedFacility.facility_name(UPDATED_FACILITY_NAME);

        restFacilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFacility.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFacility))
            )
            .andExpect(status().isOk());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getFacility_name()).isEqualTo(UPDATED_FACILITY_NAME);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository).save(testFacility);
    }

    @Test
    @Transactional
    void putNonExistingFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        facility.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facility.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facility))
            )
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(0)).save(facility);
    }

    @Test
    @Transactional
    void putWithIdMismatchFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        facility.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facility))
            )
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(0)).save(facility);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        facility.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facility)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(0)).save(facility);
    }

    @Test
    @Transactional
    void partialUpdateFacilityWithPatch() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();

        // Update the facility using partial update
        Facility partialUpdatedFacility = new Facility();
        partialUpdatedFacility.setId(facility.getId());

        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFacility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFacility))
            )
            .andExpect(status().isOk());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getFacility_name()).isEqualTo(DEFAULT_FACILITY_NAME);
    }

    @Test
    @Transactional
    void fullUpdateFacilityWithPatch() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();

        // Update the facility using partial update
        Facility partialUpdatedFacility = new Facility();
        partialUpdatedFacility.setId(facility.getId());

        partialUpdatedFacility.facility_name(UPDATED_FACILITY_NAME);

        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFacility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFacility))
            )
            .andExpect(status().isOk());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getFacility_name()).isEqualTo(UPDATED_FACILITY_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        facility.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, facility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facility))
            )
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(0)).save(facility);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        facility.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facility))
            )
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(0)).save(facility);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        facility.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(facility)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(0)).save(facility);
    }

    @Test
    @Transactional
    void deleteFacility() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        int databaseSizeBeforeDelete = facilityRepository.findAll().size();

        // Delete the facility
        restFacilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, facility.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Facility in Elasticsearch
        verify(mockFacilitySearchRepository, times(1)).deleteById(facility.getId());
    }

    @Test
    @Transactional
    void searchFacility() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        facilityRepository.saveAndFlush(facility);
        when(mockFacilitySearchRepository.search("id:" + facility.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(facility), PageRequest.of(0, 1), 1));

        // Search the facility
        restFacilityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + facility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facility.getId().intValue())))
            .andExpect(jsonPath("$.[*].facility_name").value(hasItem(DEFAULT_FACILITY_NAME)));
    }
}
