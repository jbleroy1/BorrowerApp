package com.axway.presales.borrower.web.rest;
import com.axway.presales.borrower.service.BorrowerService;
import com.axway.presales.borrower.web.rest.errors.BadRequestAlertException;
import com.axway.presales.borrower.web.rest.util.HeaderUtil;
import com.axway.presales.borrower.service.dto.BorrowerDTO;
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
 * REST controller for managing Borrower.
 */
@RestController
@RequestMapping("/api")
public class BorrowerResource {

    private final Logger log = LoggerFactory.getLogger(BorrowerResource.class);

    private static final String ENTITY_NAME = "borrowerAppBorrower";

    private final BorrowerService borrowerService;

    public BorrowerResource(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    /**
     * POST  /borrowers : Create a new borrower.
     *
     * @param borrowerDTO the borrowerDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new borrowerDTO, or with status 400 (Bad Request) if the borrower has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/borrowers")
    public ResponseEntity<BorrowerDTO> createBorrower(@Valid @RequestBody BorrowerDTO borrowerDTO) throws URISyntaxException {
        log.debug("REST request to save Borrower : {}", borrowerDTO);
        if (borrowerDTO.getId() != null) {
            throw new BadRequestAlertException("A new borrower cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BorrowerDTO result = borrowerService.save(borrowerDTO);
        return ResponseEntity.created(new URI("/api/borrowers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /borrowers : Updates an existing borrower.
     *
     * @param borrowerDTO the borrowerDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated borrowerDTO,
     * or with status 400 (Bad Request) if the borrowerDTO is not valid,
     * or with status 500 (Internal Server Error) if the borrowerDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/borrowers")
    public ResponseEntity<BorrowerDTO> updateBorrower(@Valid @RequestBody BorrowerDTO borrowerDTO) throws URISyntaxException {
        log.debug("REST request to update Borrower : {}", borrowerDTO);
        if (borrowerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BorrowerDTO result = borrowerService.save(borrowerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, borrowerDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /borrowers : get all the borrowers.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of borrowers in body
     */
    @GetMapping("/borrowers")
    public List<BorrowerDTO> getAllBorrowers() {
        log.debug("REST request to get all Borrowers");
        return borrowerService.findAll();
    }

    /**
     * GET  /borrowers/:id : get the "id" borrower.
     *
     * @param id the id of the borrowerDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the borrowerDTO, or with status 404 (Not Found)
     */
    @GetMapping("/borrowers/{id}")
    public ResponseEntity<BorrowerDTO> getBorrower(@PathVariable Long id) {
        log.debug("REST request to get Borrower : {}", id);
        Optional<BorrowerDTO> borrowerDTO = borrowerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(borrowerDTO);
    }

    /**
     * DELETE  /borrowers/:id : delete the "id" borrower.
     *
     * @param id the id of the borrowerDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/borrowers/{id}")
    public ResponseEntity<Void> deleteBorrower(@PathVariable Long id) {
        log.debug("REST request to delete Borrower : {}", id);
        borrowerService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/borrowers?query=:query : search for the borrower corresponding
     * to the query.
     *
     * @param query the query of the borrower search
     * @return the result of the search
     */
    @GetMapping("/_search/borrowers")
    public List<BorrowerDTO> searchBorrowers(@RequestParam String query) {
        log.debug("REST request to search Borrowers for query {}", query);
        return borrowerService.search(query);
    }

}
