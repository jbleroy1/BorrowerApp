package com.axway.presales.borrower.web.rest;

import com.axway.presales.borrower.BorrowerApp;

import com.axway.presales.borrower.domain.Borrower;
import com.axway.presales.borrower.repository.BorrowerRepository;
import com.axway.presales.borrower.repository.search.BorrowerSearchRepository;
import com.axway.presales.borrower.service.BorrowerService;
import com.axway.presales.borrower.service.dto.BorrowerDTO;
import com.axway.presales.borrower.service.mapper.BorrowerMapper;
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

import com.axway.presales.borrower.domain.enumeration.Gender;
/**
 * Test class for the BorrowerResource REST controller.
 *
 * @see BorrowerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BorrowerApp.class)
public class BorrowerResourceIntTest {

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final Instant DEFAULT_BIRTHDAY = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BIRTHDAY = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Autowired
    private BorrowerMapper borrowerMapper;

    @Autowired
    private BorrowerService borrowerService;

    /**
     * This repository is mocked in the com.axway.presales.borrower.repository.search test package.
     *
     * @see com.axway.presales.borrower.repository.search.BorrowerSearchRepositoryMockConfiguration
     */
    @Autowired
    private BorrowerSearchRepository mockBorrowerSearchRepository;

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

    private MockMvc restBorrowerMockMvc;

    private Borrower borrower;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BorrowerResource borrowerResource = new BorrowerResource(borrowerService);
        this.restBorrowerMockMvc = MockMvcBuilders.standaloneSetup(borrowerResource)
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
    public static Borrower createEntity(EntityManager em) {
        Borrower borrower = new Borrower()
            .firstname(DEFAULT_FIRSTNAME)
            .lastname(DEFAULT_LASTNAME)
            .age(DEFAULT_AGE)
            .gender(DEFAULT_GENDER)
            .birthday(DEFAULT_BIRTHDAY)
            .address(DEFAULT_ADDRESS);
        return borrower;
    }

    @Before
    public void initTest() {
        borrower = createEntity(em);
    }

    @Test
    @Transactional
    public void createBorrower() throws Exception {
        int databaseSizeBeforeCreate = borrowerRepository.findAll().size();

        // Create the Borrower
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);
        restBorrowerMockMvc.perform(post("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isCreated());

        // Validate the Borrower in the database
        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeCreate + 1);
        Borrower testBorrower = borrowerList.get(borrowerList.size() - 1);
        assertThat(testBorrower.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testBorrower.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testBorrower.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testBorrower.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testBorrower.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testBorrower.getAddress()).isEqualTo(DEFAULT_ADDRESS);

        // Validate the Borrower in Elasticsearch
        verify(mockBorrowerSearchRepository, times(1)).save(testBorrower);
    }

    @Test
    @Transactional
    public void createBorrowerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = borrowerRepository.findAll().size();

        // Create the Borrower with an existing ID
        borrower.setId(1L);
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBorrowerMockMvc.perform(post("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Borrower in the database
        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeCreate);

        // Validate the Borrower in Elasticsearch
        verify(mockBorrowerSearchRepository, times(0)).save(borrower);
    }

    @Test
    @Transactional
    public void checkFirstnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowerRepository.findAll().size();
        // set the field null
        borrower.setFirstname(null);

        // Create the Borrower, which fails.
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);

        restBorrowerMockMvc.perform(post("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isBadRequest());

        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowerRepository.findAll().size();
        // set the field null
        borrower.setLastname(null);

        // Create the Borrower, which fails.
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);

        restBorrowerMockMvc.perform(post("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isBadRequest());

        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAgeIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowerRepository.findAll().size();
        // set the field null
        borrower.setAge(null);

        // Create the Borrower, which fails.
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);

        restBorrowerMockMvc.perform(post("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isBadRequest());

        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowerRepository.findAll().size();
        // set the field null
        borrower.setGender(null);

        // Create the Borrower, which fails.
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);

        restBorrowerMockMvc.perform(post("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isBadRequest());

        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBirthdayIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowerRepository.findAll().size();
        // set the field null
        borrower.setBirthday(null);

        // Create the Borrower, which fails.
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);

        restBorrowerMockMvc.perform(post("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isBadRequest());

        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowerRepository.findAll().size();
        // set the field null
        borrower.setAddress(null);

        // Create the Borrower, which fails.
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);

        restBorrowerMockMvc.perform(post("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isBadRequest());

        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBorrowers() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);

        // Get all the borrowerList
        restBorrowerMockMvc.perform(get("/api/borrowers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrower.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME.toString())))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME.toString())))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())));
    }
    
    @Test
    @Transactional
    public void getBorrower() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);

        // Get the borrower
        restBorrowerMockMvc.perform(get("/api/borrowers/{id}", borrower.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(borrower.getId().intValue()))
            .andExpect(jsonPath("$.firstname").value(DEFAULT_FIRSTNAME.toString()))
            .andExpect(jsonPath("$.lastname").value(DEFAULT_LASTNAME.toString()))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.birthday").value(DEFAULT_BIRTHDAY.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBorrower() throws Exception {
        // Get the borrower
        restBorrowerMockMvc.perform(get("/api/borrowers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBorrower() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);

        int databaseSizeBeforeUpdate = borrowerRepository.findAll().size();

        // Update the borrower
        Borrower updatedBorrower = borrowerRepository.findById(borrower.getId()).get();
        // Disconnect from session so that the updates on updatedBorrower are not directly saved in db
        em.detach(updatedBorrower);
        updatedBorrower
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .age(UPDATED_AGE)
            .gender(UPDATED_GENDER)
            .birthday(UPDATED_BIRTHDAY)
            .address(UPDATED_ADDRESS);
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(updatedBorrower);

        restBorrowerMockMvc.perform(put("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isOk());

        // Validate the Borrower in the database
        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeUpdate);
        Borrower testBorrower = borrowerList.get(borrowerList.size() - 1);
        assertThat(testBorrower.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testBorrower.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testBorrower.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testBorrower.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testBorrower.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testBorrower.getAddress()).isEqualTo(UPDATED_ADDRESS);

        // Validate the Borrower in Elasticsearch
        verify(mockBorrowerSearchRepository, times(1)).save(testBorrower);
    }

    @Test
    @Transactional
    public void updateNonExistingBorrower() throws Exception {
        int databaseSizeBeforeUpdate = borrowerRepository.findAll().size();

        // Create the Borrower
        BorrowerDTO borrowerDTO = borrowerMapper.toDto(borrower);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowerMockMvc.perform(put("/api/borrowers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrowerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Borrower in the database
        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Borrower in Elasticsearch
        verify(mockBorrowerSearchRepository, times(0)).save(borrower);
    }

    @Test
    @Transactional
    public void deleteBorrower() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);

        int databaseSizeBeforeDelete = borrowerRepository.findAll().size();

        // Delete the borrower
        restBorrowerMockMvc.perform(delete("/api/borrowers/{id}", borrower.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Borrower> borrowerList = borrowerRepository.findAll();
        assertThat(borrowerList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Borrower in Elasticsearch
        verify(mockBorrowerSearchRepository, times(1)).deleteById(borrower.getId());
    }

    @Test
    @Transactional
    public void searchBorrower() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);
        when(mockBorrowerSearchRepository.search(queryStringQuery("id:" + borrower.getId())))
            .thenReturn(Collections.singletonList(borrower));
        // Search the borrower
        restBorrowerMockMvc.perform(get("/api/_search/borrowers?query=id:" + borrower.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrower.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Borrower.class);
        Borrower borrower1 = new Borrower();
        borrower1.setId(1L);
        Borrower borrower2 = new Borrower();
        borrower2.setId(borrower1.getId());
        assertThat(borrower1).isEqualTo(borrower2);
        borrower2.setId(2L);
        assertThat(borrower1).isNotEqualTo(borrower2);
        borrower1.setId(null);
        assertThat(borrower1).isNotEqualTo(borrower2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BorrowerDTO.class);
        BorrowerDTO borrowerDTO1 = new BorrowerDTO();
        borrowerDTO1.setId(1L);
        BorrowerDTO borrowerDTO2 = new BorrowerDTO();
        assertThat(borrowerDTO1).isNotEqualTo(borrowerDTO2);
        borrowerDTO2.setId(borrowerDTO1.getId());
        assertThat(borrowerDTO1).isEqualTo(borrowerDTO2);
        borrowerDTO2.setId(2L);
        assertThat(borrowerDTO1).isNotEqualTo(borrowerDTO2);
        borrowerDTO1.setId(null);
        assertThat(borrowerDTO1).isNotEqualTo(borrowerDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(borrowerMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(borrowerMapper.fromId(null)).isNull();
    }
}
