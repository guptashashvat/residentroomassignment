package org.jhipster.facility.repository;

import org.jhipster.facility.domain.Facility;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Facility entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {}
