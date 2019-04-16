package com.axway.presales.borrower.service;

import com.axway.presales.borrower.service.dto.CreditDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Credit.
 */
public interface CreditService {

    /**
     * Save a credit.
     *
     * @param creditDTO the entity to save
     * @return the persisted entity
     */
    CreditDTO save(CreditDTO creditDTO);

    /**
     * Get all the credits.
     *
     * @return the list of entities
     */
    List<CreditDTO> findAll();


    /**
     * Get the "id" credit.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CreditDTO> findOne(Long id);

    /**
     * Delete the "id" credit.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the credit corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<CreditDTO> search(String query);
}
