package com.axway.presales.borrower.web.rest;

import com.axway.presales.borrower.BorrowerApp;

import com.axway.presales.borrower.domain.Credit;
import com.axway.presales.borrower.repository.CreditRepository;
import com.axway.presales.borrower.repository.search.CreditSearchRepository;
import com.axway.presales.borrower.service.CreditService;
import com.axway.presales.borrower.service.dto.CreditDTO;
import com.axway.presales.borrower.service.mapper.CreditMapper;
import com.axway.presales.borrower.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;


import static com.axway.presales.borrower.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axway.presales.borrower.domain.enumeration.Status;
/**
 * Test class for the CreditResource REST controller.
 *
 * @see CreditResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BorrowerApp.class)
public class CreditResourceIntTest {

    private static final String DEFAULT_AMOUNT = "AAAAAAAAAA";
    private static final String UPDATED_AMOUNT = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_DURATION = "AAAAAAAAAA";
    private static final String UPDATED_DURATION = "BBBBBBBBBB";

    private static final Instant DEFAULT_WHY = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_WHY = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_BORROWER = "AAAAAAAAAA";
    private static final String UPDATED_BORROWER = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.REFUSED;
    private static final Status UPDATED_STATUS = Status.ACCEPTED;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private CreditMapper creditMapper;

    @Autowired
    private CreditService creditService;

    /**
     * This repository is mocked in the com.axway.presales.borrower.repository.search test package.
     *
     * @see com.axway.presales.borrower.repository.search.CreditSearchRepositoryMockConfiguration
     */
    @Autowired
    private CreditSearchRepository mockCreditSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restCreditMockMvc;

    private Credit credit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CreditResource creditResource = new CreditResource(creditService);
        this.restCreditMockMvc = MockMvcBuilders.standaloneSetup(creditResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Credit createEntity(EntityManager em) {
        Credit credit = new Credit()
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .duration(DEFAULT_DURATION)
            .why(DEFAULT_WHY)
            .borrower(DEFAULT_BORROWER)
            .status(DEFAULT_STATUS);
        return credit;
    }

    @Before
    public void initTest() {
        credit = createEntity(em);
    }

    @Test
    @Transactional
    public void createCredit() throws Exception {
        int databaseSizeBeforeCreate = creditRepository.findAll().size();

        // Create the Credit
        CreditDTO creditDTO = creditMapper.toDto(credit);
        restCreditMockMvc.perform(post("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isCreated());

        // Validate the Credit in the database
        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeCreate + 1);
        Credit testCredit = creditList.get(creditList.size() - 1);
        assertThat(testCredit.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testCredit.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testCredit.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testCredit.getWhy()).isEqualTo(DEFAULT_WHY);
        assertThat(testCredit.getBorrower()).isEqualTo(DEFAULT_BORROWER);
        assertThat(testCredit.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Credit in Elasticsearch
        verify(mockCreditSearchRepository, times(1)).save(testCredit);
    }

    @Test
    @Transactional
    public void createCreditWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = creditRepository.findAll().size();

        // Create the Credit with an existing ID
        credit.setId(1L);
        CreditDTO creditDTO = creditMapper.toDto(credit);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCreditMockMvc.perform(post("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Credit in the database
        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeCreate);

        // Validate the Credit in Elasticsearch
        verify(mockCreditSearchRepository, times(0)).save(credit);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = creditRepository.findAll().size();
        // set the field null
        credit.setAmount(null);

        // Create the Credit, which fails.
        CreditDTO creditDTO = creditMapper.toDto(credit);

        restCreditMockMvc.perform(post("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isBadRequest());

        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = creditRepository.findAll().size();
        // set the field null
        credit.setCurrency(null);

        // Create the Credit, which fails.
        CreditDTO creditDTO = creditMapper.toDto(credit);

        restCreditMockMvc.perform(post("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isBadRequest());

        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = creditRepository.findAll().size();
        // set the field null
        credit.setDuration(null);

        // Create the Credit, which fails.
        CreditDTO creditDTO = creditMapper.toDto(credit);

        restCreditMockMvc.perform(post("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isBadRequest());

        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWhyIsRequired() throws Exception {
        int databaseSizeBeforeTest = creditRepository.findAll().size();
        // set the field null
        credit.setWhy(null);

        // Create the Credit, which fails.
        CreditDTO creditDTO = creditMapper.toDto(credit);

        restCreditMockMvc.perform(post("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isBadRequest());

        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBorrowerIsRequired() throws Exception {
        int databaseSizeBeforeTest = creditRepository.findAll().size();
        // set the field null
        credit.setBorrower(null);

        // Create the Credit, which fails.
        CreditDTO creditDTO = creditMapper.toDto(credit);

        restCreditMockMvc.perform(post("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isBadRequest());

        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = creditRepository.findAll().size();
        // set the field null
        credit.setStatus(null);

        // Create the Credit, which fails.
        CreditDTO creditDTO = creditMapper.toDto(credit);

        restCreditMockMvc.perform(post("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isBadRequest());

        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCredits() throws Exception {
        // Initialize the database
        creditRepository.saveAndFlush(credit);

        // Get all the creditList
        restCreditMockMvc.perform(get("/api/credits?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(credit.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.toString())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())))
            .andExpect(jsonPath("$.[*].why").value(hasItem(DEFAULT_WHY.toString())))
            .andExpect(jsonPath("$.[*].borrower").value(hasItem(DEFAULT_BORROWER.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getCredit() throws Exception {
        // Initialize the database
        creditRepository.saveAndFlush(credit);

        // Get the credit
        restCreditMockMvc.perform(get("/api/credits/{id}", credit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(credit.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.toString()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.toString()))
            .andExpect(jsonPath("$.why").value(DEFAULT_WHY.toString()))
            .andExpect(jsonPath("$.borrower").value(DEFAULT_BORROWER.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCredit() throws Exception {
        // Get the credit
        restCreditMockMvc.perform(get("/api/credits/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCredit() throws Exception {
        // Initialize the database
        creditRepository.saveAndFlush(credit);

        int databaseSizeBeforeUpdate = creditRepository.findAll().size();

        // Update the credit
        Credit updatedCredit = creditRepository.findById(credit.getId()).get();
        // Disconnect from session so that the updates on updatedCredit are not directly saved in db
        em.detach(updatedCredit);
        updatedCredit
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .duration(UPDATED_DURATION)
            .why(UPDATED_WHY)
            .borrower(UPDATED_BORROWER)
            .status(UPDATED_STATUS);
        CreditDTO creditDTO = creditMapper.toDto(updatedCredit);

        restCreditMockMvc.perform(put("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isOk());

        // Validate the Credit in the database
        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeUpdate);
        Credit testCredit = creditList.get(creditList.size() - 1);
        assertThat(testCredit.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testCredit.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testCredit.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testCredit.getWhy()).isEqualTo(UPDATED_WHY);
        assertThat(testCredit.getBorrower()).isEqualTo(UPDATED_BORROWER);
        assertThat(testCredit.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Credit in Elasticsearch
        verify(mockCreditSearchRepository, times(1)).save(testCredit);
    }

    @Test
    @Transactional
    public void updateNonExistingCredit() throws Exception {
        int databaseSizeBeforeUpdate = creditRepository.findAll().size();

        // Create the Credit
        CreditDTO creditDTO = creditMapper.toDto(credit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCreditMockMvc.perform(put("/api/credits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(creditDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Credit in the database
        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Credit in Elasticsearch
        verify(mockCreditSearchRepository, times(0)).save(credit);
    }

    @Test
    @Transactional
    public void deleteCredit() throws Exception {
        // Initialize the database
        creditRepository.saveAndFlush(credit);

        int databaseSizeBeforeDelete = creditRepository.findAll().size();

        // Delete the credit
        restCreditMockMvc.perform(delete("/api/credits/{id}", credit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Credit> creditList = creditRepository.findAll();
        assertThat(creditList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Credit in Elasticsearch
        verify(mockCreditSearchRepository, times(1)).deleteById(credit.getId());
    }

    @Test
    @Transactional
    public void searchCredit() throws Exception {
        // Initialize the database
        creditRepository.saveAndFlush(credit);
        when(mockCreditSearchRepository.search(queryStringQuery("id:" + credit.getId())))
            .thenReturn(Collections.singletonList(credit));
        // Search the credit
        restCreditMockMvc.perform(get("/api/_search/credits?query=id:" + credit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(credit.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].why").value(hasItem(DEFAULT_WHY.toString())))
            .andExpect(jsonPath("$.[*].borrower").value(hasItem(DEFAULT_BORROWER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Credit.class);
        Credit credit1 = new Credit();
        credit1.setId(1L);
        Credit credit2 = new Credit();
        credit2.setId(credit1.getId());
        assertThat(credit1).isEqualTo(credit2);
        credit2.setId(2L);
        assertThat(credit1).isNotEqualTo(credit2);
        credit1.setId(null);
        assertThat(credit1).isNotEqualTo(credit2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CreditDTO.class);
        CreditDTO creditDTO1 = new CreditDTO();
        creditDTO1.setId(1L);
        CreditDTO creditDTO2 = new CreditDTO();
        assertThat(creditDTO1).isNotEqualTo(creditDTO2);
        creditDTO2.setId(creditDTO1.getId());
        assertThat(creditDTO1).isEqualTo(creditDTO2);
        creditDTO2.setId(2L);
        assertThat(creditDTO1).isNotEqualTo(creditDTO2);
        creditDTO1.setId(null);
        assertThat(creditDTO1).isNotEqualTo(creditDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(creditMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(creditMapper.fromId(null)).isNull();
    }
}
