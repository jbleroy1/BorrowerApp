package com.axway.presales.borrower.web.rest;
import com.axway.presales.borrower.service.CreditService;
import com.axway.presales.borrower.web.rest.errors.BadRequestAlertException;
import com.axway.presales.borrower.web.rest.util.HeaderUtil;
import com.axway.presales.borrower.service.dto.CreditDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Credit.
 */
@RestController
@RequestMapping("/api")
public class CreditResource {

    private final Logger log = LoggerFactory.getLogger(CreditResource.class);

    private static final String ENTITY_NAME = "borrowerAppCredit";

    private final CreditService creditService;

    public CreditResource(CreditService creditService) {
        this.creditService = creditService;
    }

    /**
     * POST  /credits : Create a new credit.
     *
     * @param creditDTO the creditDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new creditDTO, or with status 400 (Bad Request) if the credit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/credits")
    public ResponseEntity<CreditDTO> createCredit(@Valid @RequestBody CreditDTO creditDTO) throws URISyntaxException {
        log.debug("REST request to save Credit : {}", creditDTO);
        if (creditDTO.getId() != null) {
            throw new BadRequestAlertException("A new credit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CreditDTO result = creditService.save(creditDTO);
        return ResponseEntity.created(new URI("/api/credits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /credits : Updates an existing credit.
     *
     * @param creditDTO the creditDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated creditDTO,
     * or with status 400 (Bad Request) if the creditDTO is not valid,
     * or with status 500 (Internal Server Error) if the creditDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/credits")
    public ResponseEntity<CreditDTO> updateCredit(@Valid @RequestBody CreditDTO creditDTO) throws URISyntaxException {
        log.debug("REST request to update Credit : {}", creditDTO);
        if (creditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CreditDTO result = creditService.save(creditDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, creditDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /credits : get all the credits.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of credits in body
     */
    @GetMapping("/credits")
    public List<CreditDTO> getAllCredits() {
        log.debug("REST request to get all Credits");
        return creditService.findAll();
    }

    /**
     * GET  /credits/:id : get the "id" credit.
     *
     * @param id the id of the creditDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the creditDTO, or with status 404 (Not Found)
     */
    @GetMapping("/credits/{id}")
    public ResponseEntity<CreditDTO> getCredit(@PathVariable Long id) {
        log.debug("REST request to get Credit : {}", id);
        Optional<CreditDTO> creditDTO = creditService.findOne(id);
        return ResponseUtil.wrapOrNotFound(creditDTO);
    }

    /**
     * DELETE  /credits/:id : delete the "id" credit.
     *
     * @param id the id of the creditDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/credits/{id}")
    public ResponseEntity<Void> deleteCredit(@PathVariable Long id) {
        log.debug("REST request to delete Credit : {}", id);
        creditService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/credits?query=:query : search for the credit corresponding
     * to the query.
     *
     * @param query the query of the credit search
     * @return the result of the search
     */
    @GetMapping("/_search/credits")
    public List<CreditDTO> searchCredits(@RequestParam String query) {
        log.debug("REST request to search Credits for query {}", query);
        return creditService.search(query);
    }

}
