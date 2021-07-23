package com.my.rental.web.rest;

import com.my.rental.service.RentalService;
import com.my.rental.web.rest.errors.BadRequestAlertException;
import com.my.rental.service.dto.RentalDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.my.rental.domain.Rental}.
 */
@RestController
@RequestMapping("/api")
public class RentalResource {

    private final Logger log = LoggerFactory.getLogger(RentalResource.class);

    private static final String ENTITY_NAME = "rentalRental";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RentalService rentalService;

    public RentalResource(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * {@code POST  /rentals} : Create a new rental.
     *
     * @param rentalDTO the rentalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rentalDTO, or with status {@code 400 (Bad Request)} if the rental has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rentals")
    public ResponseEntity<RentalDTO> createRental(@RequestBody RentalDTO rentalDTO) throws URISyntaxException {
        log.debug("REST request to save Rental : {}", rentalDTO);
        if (rentalDTO.getId() != null) {
            throw new BadRequestAlertException("A new rental cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RentalDTO result = rentalService.save(rentalDTO);
        return ResponseEntity.created(new URI("/api/rentals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rentals} : Updates an existing rental.
     *
     * @param rentalDTO the rentalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rentalDTO,
     * or with status {@code 400 (Bad Request)} if the rentalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rentalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rentals")
    public ResponseEntity<RentalDTO> updateRental(@RequestBody RentalDTO rentalDTO) throws URISyntaxException {
        log.debug("REST request to update Rental : {}", rentalDTO);
        if (rentalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RentalDTO result = rentalService.save(rentalDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rentalDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /rentals} : get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rentals in body.
     */
    @GetMapping("/rentals")
    public ResponseEntity<List<RentalDTO>> getAllRentals(Pageable pageable) {
        log.debug("REST request to get a page of Rentals");
        Page<RentalDTO> page = rentalService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rentals/:id} : get the "id" rental.
     *
     * @param id the id of the rentalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rentalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rentals/{id}")
    public ResponseEntity<RentalDTO> getRental(@PathVariable Long id) {
        log.debug("REST request to get Rental : {}", id);
        Optional<RentalDTO> rentalDTO = rentalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rentalDTO);
    }

    /**
     * {@code DELETE  /rentals/:id} : delete the "id" rental.
     *
     * @param id the id of the rentalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rentals/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        log.debug("REST request to delete Rental : {}", id);
        rentalService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * 도서 대출 API
     * @param userid
     * @param bookId
     * @return
     */
    @PostMapping("/rentals/{userid}/RentedItem/{book}")
    public ResponseEntity<RentalDTO> rentBooks(@PathVariable("userid") Long userid, @PathVariable("book") Long bookId)
        throws InterruptedException, ExecutionException, JsonProcessingException, RentUnavailableException {
        log.debug("rent book request");

        //도서 서비스를 호출해 도서 정보 가져오기
        ResponseEntity<BookInfoDTO> bookInfoResult = bookClient.findBookInfo(bookId); //feign - 책 정보 가져오기
        BookInfoDTO bookInfoDTO = bookInfoResult.getBody();
        log.debug("book info list", bookInfoDTO.toString());

        Rental rental= rentalService.rentBook(userid, bookInfoDTO.getId(), bookInfoDTO.getTitle());
        RentalDTO rentalDTO = rentalMapper.toDto(rental);
        return ResponseEntity.ok().body(rentalDTO);

    }

    /**
     * 도서 반납 API
     *
     * @param userid
     * @param book
     * @return
     */
    @DeleteMapping("/rentals/{userid}/RentedItem/{book}")
    public ResponseEntity returnBooks(@PathVariable("userid") Long userid, @PathVariable("book") Long book) throws InterruptedException, ExecutionException, JsonProcessingException {
        Rental rental = rentalService.returnBook(userid, book);
        log.debug("returned books");
        log.debug("SEND BOOKIDS for Book: {}", book);

        RentalDTO result = rentalMapper.toDto(rental);
        return ResponseEntity.ok().body(result);
    }
}
