package org.jhipster.facility.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Resident.
 */
@Entity
@Table(name = "resident")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "resident")
public class Resident implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Max(value = 9999999999999L)
    @Column(name = "phone_number", nullable = false, unique = true)
    private Integer phone_number;

    @Column(name = "email")
    private String email;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "facility" }, allowSetters = true)
    private Room room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Resident id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Resident name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPhone_number() {
        return this.phone_number;
    }

    public Resident phone_number(Integer phone_number) {
        this.setPhone_number(phone_number);
        return this;
    }

    public void setPhone_number(Integer phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return this.email;
    }

    public Resident email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Resident room(Room room) {
        this.setRoom(room);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resident)) {
            return false;
        }
        return id != null && id.equals(((Resident) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Resident{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", phone_number=" + getPhone_number() +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
