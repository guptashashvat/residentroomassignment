package org.jhipster.facility.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Facility.
 */
@Entity
@Table(name = "facility")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "facility")
public class Facility implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "facility_name", length = 200, nullable = false, unique = true)
    private String facility_name;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Facility id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFacility_name() {
        return this.facility_name;
    }

    public Facility facility_name(String facility_name) {
        this.setFacility_name(facility_name);
        return this;
    }

    public void setFacility_name(String facility_name) {
        this.facility_name = facility_name;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Facility)) {
            return false;
        }
        return id != null && id.equals(((Facility) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Facility{" +
            "id=" + getId() +
            ", facility_name='" + getFacility_name() + "'" +
            "}";
    }
}
