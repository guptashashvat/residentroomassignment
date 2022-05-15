package org.jhipster.facility.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Room.
 */
@Entity
@Table(name = "room")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "room")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Max(value = 10000)
    @Column(name = "room_number", nullable = false)
    private Integer room_number;

    @ManyToOne(optional = false)
    @NotNull
    private Facility facility;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Room id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRoom_number() {
        return this.room_number;
    }

    public Room room_number(Integer room_number) {
        this.setRoom_number(room_number);
        return this;
    }

    public void setRoom_number(Integer room_number) {
        this.room_number = room_number;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Room facility(Facility facility) {
        this.setFacility(facility);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        return id != null && id.equals(((Room) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Room{" +
            "id=" + getId() +
            ", room_number=" + getRoom_number() +
            "}";
    }
}
