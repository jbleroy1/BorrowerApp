package com.axway.presales.borrower.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.axway.presales.borrower.domain.enumeration.Status;

/**
 * A Credit.
 */
@Entity
@Table(name = "credit")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "credit")
public class Credit implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private String amount;

    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @NotNull
    @Column(name = "duration", nullable = false)
    private String duration;

    @NotNull
    @Column(name = "why", nullable = false)
    private Instant why;

    @NotNull
    @Column(name = "borrower", nullable = false)
    private String borrower;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public Credit amount(String amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Credit currency(String currency) {
        this.currency = currency;
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDuration() {
        return duration;
    }

    public Credit duration(String duration) {
        this.duration = duration;
        return this;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Instant getWhy() {
        return why;
    }

    public Credit why(Instant why) {
        this.why = why;
        return this;
    }

    public void setWhy(Instant why) {
        this.why = why;
    }

    public String getBorrower() {
        return borrower;
    }

    public Credit borrower(String borrower) {
        this.borrower = borrower;
        return this;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public Status getStatus() {
        return status;
    }

    public Credit status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Credit credit = (Credit) o;
        if (credit.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), credit.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Credit{" +
            "id=" + getId() +
            ", amount='" + getAmount() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", duration='" + getDuration() + "'" +
            ", why='" + getWhy() + "'" +
            ", borrower='" + getBorrower() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
