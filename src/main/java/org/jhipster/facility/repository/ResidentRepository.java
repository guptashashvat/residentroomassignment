package org.jhipster.facility.repository;

import java.util.List;
import java.util.Optional;
import org.jhipster.facility.domain.Resident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Resident entity.
 */
@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    default Optional<Resident> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Resident> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Resident> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct resident from Resident resident left join fetch resident.room",
        countQuery = "select count(distinct resident) from Resident resident"
    )
    Page<Resident> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct resident from Resident resident left join fetch resident.room")
    List<Resident> findAllWithToOneRelationships();

    @Query("select resident from Resident resident left join fetch resident.room where resident.id =:id")
    Optional<Resident> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = "select r from Resident r where r.room.id =:room_id",
        countQuery = "select count(r) from Resident r where r.room.id=:room_id"
    )
    Page<Resident> findWithRoomId(Pageable pageable, @Param("room_id") Long room_id);
}
