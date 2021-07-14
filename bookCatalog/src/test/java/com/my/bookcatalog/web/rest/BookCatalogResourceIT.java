package com.my.bookcatalog.web.rest;

import com.my.bookcatalog.BookCatalogApp;
import com.my.bookcatalog.domain.BookCatalog;
import com.my.bookcatalog.repository.BookCatalogRepository;
import com.my.bookcatalog.service.BookCatalogService;
import com.my.bookcatalog.service.dto.BookCatalogDTO;
import com.my.bookcatalog.service.mapper.BookCatalogMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BookCatalogResource} REST controller.
 */
@SpringBootTest(classes = BookCatalogApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class BookCatalogResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private BookCatalogRepository bookCatalogRepository;

    @Autowired
    private BookCatalogMapper bookCatalogMapper;

    @Autowired
    private BookCatalogService bookCatalogService;

    @Autowired
    private MockMvc restBookCatalogMockMvc;

    private BookCatalog bookCatalog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCatalog createEntity() {
        BookCatalog bookCatalog = new BookCatalog()
            .title(DEFAULT_TITLE)
            .author(DEFAULT_AUTHOR)
            .description(DEFAULT_DESCRIPTION);
        return bookCatalog;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCatalog createUpdatedEntity() {
        BookCatalog bookCatalog = new BookCatalog()
            .title(UPDATED_TITLE)
            .author(UPDATED_AUTHOR)
            .description(UPDATED_DESCRIPTION);
        return bookCatalog;
    }

    @BeforeEach
    public void initTest() {
        bookCatalogRepository.deleteAll();
        bookCatalog = createEntity();
    }

    @Test
    public void createBookCatalog() throws Exception {
        int databaseSizeBeforeCreate = bookCatalogRepository.findAll().size();
        // Create the BookCatalog
        BookCatalogDTO bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog);
        restBookCatalogMockMvc.perform(post("/api/book-catalogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bookCatalogDTO)))
            .andExpect(status().isCreated());

        // Validate the BookCatalog in the database
        List<BookCatalog> bookCatalogList = bookCatalogRepository.findAll();
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeCreate + 1);
        BookCatalog testBookCatalog = bookCatalogList.get(bookCatalogList.size() - 1);
        assertThat(testBookCatalog.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBookCatalog.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testBookCatalog.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    public void createBookCatalogWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookCatalogRepository.findAll().size();

        // Create the BookCatalog with an existing ID
        bookCatalog.setId("existing_id");
        BookCatalogDTO bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookCatalogMockMvc.perform(post("/api/book-catalogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bookCatalogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookCatalog in the database
        List<BookCatalog> bookCatalogList = bookCatalogRepository.findAll();
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void getAllBookCatalogs() throws Exception {
        // Initialize the database
        bookCatalogRepository.save(bookCatalog);

        // Get all the bookCatalogList
        restBookCatalogMockMvc.perform(get("/api/book-catalogs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCatalog.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    public void getBookCatalog() throws Exception {
        // Initialize the database
        bookCatalogRepository.save(bookCatalog);

        // Get the bookCatalog
        restBookCatalogMockMvc.perform(get("/api/book-catalogs/{id}", bookCatalog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookCatalog.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }
    @Test
    public void getNonExistingBookCatalog() throws Exception {
        // Get the bookCatalog
        restBookCatalogMockMvc.perform(get("/api/book-catalogs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateBookCatalog() throws Exception {
        // Initialize the database
        bookCatalogRepository.save(bookCatalog);

        int databaseSizeBeforeUpdate = bookCatalogRepository.findAll().size();

        // Update the bookCatalog
        BookCatalog updatedBookCatalog = bookCatalogRepository.findById(bookCatalog.getId()).get();
        updatedBookCatalog
            .title(UPDATED_TITLE)
            .author(UPDATED_AUTHOR)
            .description(UPDATED_DESCRIPTION);
        BookCatalogDTO bookCatalogDTO = bookCatalogMapper.toDto(updatedBookCatalog);

        restBookCatalogMockMvc.perform(put("/api/book-catalogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bookCatalogDTO)))
            .andExpect(status().isOk());

        // Validate the BookCatalog in the database
        List<BookCatalog> bookCatalogList = bookCatalogRepository.findAll();
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate);
        BookCatalog testBookCatalog = bookCatalogList.get(bookCatalogList.size() - 1);
        assertThat(testBookCatalog.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBookCatalog.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testBookCatalog.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    public void updateNonExistingBookCatalog() throws Exception {
        int databaseSizeBeforeUpdate = bookCatalogRepository.findAll().size();

        // Create the BookCatalog
        BookCatalogDTO bookCatalogDTO = bookCatalogMapper.toDto(bookCatalog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCatalogMockMvc.perform(put("/api/book-catalogs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bookCatalogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookCatalog in the database
        List<BookCatalog> bookCatalogList = bookCatalogRepository.findAll();
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteBookCatalog() throws Exception {
        // Initialize the database
        bookCatalogRepository.save(bookCatalog);

        int databaseSizeBeforeDelete = bookCatalogRepository.findAll().size();

        // Delete the bookCatalog
        restBookCatalogMockMvc.perform(delete("/api/book-catalogs/{id}", bookCatalog.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BookCatalog> bookCatalogList = bookCatalogRepository.findAll();
        assertThat(bookCatalogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
