package com.axway.presales.borrower.service.dto;
import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.axway.presales.borrower.domain.enumeration.Status;

/**
 * A DTO for the Credit entity.
 */
public class CreditDTO implements Serializable {

    private Long id;

    @NotNull
    private String amount;

    @NotNull
    private String currency;

    @NotNull
    private String duration;

    @NotNull
    private Instant why;

    @NotNull
    private String borrower;

    @NotNull
    private Status status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Instant getWhy() {
        return why;
    }

    public void setWhy(Instant why) {
        this.why = why;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CreditDTO creditDTO = (CreditDTO) o;
        if (creditDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), creditDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CreditDTO{" +
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
