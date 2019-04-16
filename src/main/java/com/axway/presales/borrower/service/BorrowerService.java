package com.axway.presales.borrower.service;

import com.axway.presales.borrower.service.dto.BorrowerDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Borrower.
 */
public interface BorrowerService {

    /**
     * Save a borrower.
     *
     * @param borrowerDTO the entity to save
     * @return the persisted entity
     */
    BorrowerDTO save(BorrowerDTO borrowerDTO);

    /**
     * Get all the borrowers.
     *
     * @return the list of entities
     */
    List<BorrowerDTO> findAll();


    /**
     * Get the "id" borrower.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<BorrowerDTO> findOne(Long id);

    /**
     * Delete the "id" borrower.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the borrower corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<BorrowerDTO> search(String query);
}
