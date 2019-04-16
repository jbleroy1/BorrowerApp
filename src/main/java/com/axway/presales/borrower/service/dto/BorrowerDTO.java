package com.axway.presales.borrower.service.dto;
import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.axway.presales.borrower.domain.enumeration.Gender;

/**
 * A DTO for the Borrower entity.
 */
public class BorrowerDTO implements Serializable {

    private Long id;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    private Integer age;

    @NotNull
    private Gender gender;

    @NotNull
    private Instant birthday;

    @NotNull
    private String address;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Instant getBirthday() {
        return birthday;
    }

    public void setBirthday(Instant birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BorrowerDTO borrowerDTO = (BorrowerDTO) o;
        if (borrowerDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), borrowerDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BorrowerDTO{" +
            "id=" + getId() +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            ", age=" + getAge() +
            ", gender='" + getGender() + "'" +
            ", birthday='" + getBirthday() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }
}
