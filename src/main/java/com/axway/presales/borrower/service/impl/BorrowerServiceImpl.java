package com.axway.presales.borrower.service.impl;

import com.axway.presales.borrower.service.BorrowerService;
import com.axway.presales.borrower.domain.Borrower;
import com.axway.presales.borrower.repository.BorrowerRepository;
import com.axway.presales.borrower.repository.search.BorrowerSearchRepository;
import com.axway.presales.borrower.service.dto.BorrowerDTO;
import com.axway.presales.borrower.service.mapper.BorrowerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Borrower.
 */
@Service
@Transactional
public class BorrowerServiceImpl implements BorrowerService {

    private final Logger log = LoggerFactory.getLogger(BorrowerServiceImpl.class);

    private final BorrowerRepository borrowerRepository;

    private final BorrowerMapper borrowerMapper;

    private final BorrowerSearchRepository borrowerSearchRepository;

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository, BorrowerMapper borrowerMapper, BorrowerSearchRepository borrowerSearchRepository) {
        this.borrowerRepository = borrowerRepository;
        this.borrowerMapper = borrowerMapper;
        this.borrowerSearchRepository = borrowerSearchRepository;
    }

    /**
     * Save a borrower.
     *
     * @param borrowerDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public BorrowerDTO save(BorrowerDTO borrowerDTO) {
        log.debug("Request to save Borrower : {}", borrowerDTO);
        Borrower borrower = borrowerMapper.toEntity(borrowerDTO);
        borrower = borrowerRepository.save(borrower);
        BorrowerDTO result = borrowerMapper.toDto(borrower);
        borrowerSearchRepository.save(borrower);
        return result;
    }

    /**
     * Get all the borrowers.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<BorrowerDTO> findAll() {
        log.debug("Request to get all Borrowers");
        return borrowerRepository.findAll().stream()
            .map(borrowerMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one borrower by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<BorrowerDTO> findOne(Long id) {
        log.debug("Request to get Borrower : {}", id);
        return borrowerRepository.findById(id)
            .map(borrowerMapper::toDto);
    }

    /**
     * Delete the borrower by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Borrower : {}", id);
        borrowerRepository.deleteById(id);
        borrowerSearchRepository.deleteById(id);
    }

    /**
     * Search for the borrower corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<BorrowerDTO> search(String query) {
        log.debug("Request to search Borrowers for query {}", query);
        return StreamSupport
            .stream(borrowerSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(borrowerMapper::toDto)
            .collect(Collectors.toList());
    }
}
