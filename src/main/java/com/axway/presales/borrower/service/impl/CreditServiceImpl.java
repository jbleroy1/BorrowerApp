package com.axway.presales.borrower.service.impl;

import com.axway.presales.borrower.service.CreditService;
import com.axway.presales.borrower.domain.Credit;
import com.axway.presales.borrower.repository.CreditRepository;
import com.axway.presales.borrower.repository.search.CreditSearchRepository;
import com.axway.presales.borrower.service.dto.CreditDTO;
import com.axway.presales.borrower.service.mapper.CreditMapper;
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
 * Service Implementation for managing Credit.
 */
@Service
@Transactional
public class CreditServiceImpl implements CreditService {

    private final Logger log = LoggerFactory.getLogger(CreditServiceImpl.class);

    private final CreditRepository creditRepository;

    private final CreditMapper creditMapper;

    private final CreditSearchRepository creditSearchRepository;

    public CreditServiceImpl(CreditRepository creditRepository, CreditMapper creditMapper, CreditSearchRepository creditSearchRepository) {
        this.creditRepository = creditRepository;
        this.creditMapper = creditMapper;
        this.creditSearchRepository = creditSearchRepository;
    }

    /**
     * Save a credit.
     *
     * @param creditDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public CreditDTO save(CreditDTO creditDTO) {
        log.debug("Request to save Credit : {}", creditDTO);
        Credit credit = creditMapper.toEntity(creditDTO);
        credit = creditRepository.save(credit);
        CreditDTO result = creditMapper.toDto(credit);
        creditSearchRepository.save(credit);
        return result;
    }

    /**
     * Get all the credits.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<CreditDTO> findAll() {
        log.debug("Request to get all Credits");
        return creditRepository.findAll().stream()
            .map(creditMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one credit by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CreditDTO> findOne(Long id) {
        log.debug("Request to get Credit : {}", id);
        return creditRepository.findById(id)
            .map(creditMapper::toDto);
    }

    /**
     * Delete the credit by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Credit : {}", id);
        creditRepository.deleteById(id);
        creditSearchRepository.deleteById(id);
    }

    /**
     * Search for the credit corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<CreditDTO> search(String query) {
        log.debug("Request to search Credits for query {}", query);
        return StreamSupport
            .stream(creditSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(creditMapper::toDto)
            .collect(Collectors.toList());
    }
}
