package org.jhipster.facility.repository;

import java.util.List;
import java.util.Optional;
import org.jhipster.facility.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Room entity.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    default Optional<Room> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Room> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Room> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct room from Room room left join fetch room.facility",
        countQuery = "select count(distinct room) from Room room"
    )
    Page<Room> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct room from Room room left join fetch room.facility")
    List<Room> findAllWithToOneRelationships();

    @Query("select room from Room room left join fetch room.facility where room.id =:id")
    Optional<Room> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = "select r from Room r where r.facility.id =:facility_id",
        countQuery = "select count(r) from Room r where r.facility.id=:facility_id"
    )
    Page<Room> findWithFacilityId(Pageable pageable, @Param("facility_id") Long facility_id);
}
