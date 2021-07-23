package com.my.rental.service.impl;

import com.my.rental.service.RentalService;
import com.my.rental.domain.Rental;
import com.my.rental.repository.RentalRepository;
import com.my.rental.service.dto.RentalDTO;
import com.my.rental.service.mapper.RentalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Rental}.
 */
@Service
@Transactional
public class RentalServiceImpl implements RentalService {

    private final Logger log = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final RentalRepository rentalRepository;

    private final RentalMapper rentalMapper;

    public RentalServiceImpl(RentalRepository rentalRepository, RentalMapper rentalMapper) {
        this.rentalRepository = rentalRepository;
        this.rentalMapper = rentalMapper;
    }

    /**
     * Save a rental.
     *
     * @param rentalDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public RentalDTO save(RentalDTO rentalDTO) {
        log.debug("Request to save Rental : {}", rentalDTO);
        Rental rental = rentalMapper.toEntity(rentalDTO);
        rental = rentalRepository.save(rental);
        return rentalMapper.toDto(rental);
    }

    /**
     * Get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RentalDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Rentals");
        return rentalRepository.findAll(pageable)
            .map(rentalMapper::toDto);
    }


    /**
     * Get one rental by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RentalDTO> findOne(Long id) {
        log.debug("Request to get Rental : {}", id);
        return rentalRepository.findById(id)
            .map(rentalMapper::toDto);
    }

    /**
     * Delete the rental by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Rental : {}", id);
        rentalRepository.deleteById(id);
    }

    //도서 대출 처리 구현
    @Transactional
    public Rental rentBook(Long userId, Long bookId, String bookTitle) throws InterruptedException, ExecutionException, JsonProcessingException, RentUnavailableException {
        log.debug("Rent Books by : ", userId, " Book List : ", bookId + bookTitle);
        Rental rental = rentalRepository.findByUserId(userId).get();
        rental.checkRentalAvailable();

        rental = rental.rentBook(bookId, bookTitle);
        rentalRepository.save(rental);

        //도서 서비스엥 도서재고 감소를 위해 도서대출 이벤트 발송
        rentalProducer.updateBookStatus(bookId, "UNAVAILABLE"); //send to book service

        //도서 카탈로그 서비스에 대출된 도서로 상태를 변경하기 위한 이벤트 발송
        rentalProducer.updateBookCatalogStatus(bookId, "RENT_BOOK"); //send to book catalog service

        //대출로 인한 사용자 포잉ㄴ트 적립을 위해 사용자 서비스에 이벤트 발송
        rentalProducer.savePoints(userId, pointPerBooks); //send to user service

        return rental;

    }

    // 도서 반납 구현
    @Transactional
    public Rental returnBook(Long userId, Long bookId) throws ExecutionException, InterruptedException ,JsonProcessingException {
        log.debug("Return books by ", userId, " Return Book List : ", bookId);
        Rental rental = rentalRepository.findByUserId(userId).get(); //반납아이템 검색(1)
        rental = rental.returnbook(bookId);                 //Rental에 반납 처리 위임(2)
        rental = rentalRepository.save(rental);                         //Rental 저장(3)

        //도서 서비스에 도서재고 증가를 위해 도서반납 이벤트 발송
        rentalProducer.updateBookStatus(bookId, "AVAILABLE");                   //(4)
        //도서 카탈로그 서비스에 대출 가능한 도서로 상태를 변경하기 위한 이벤트 발송
        rentalProducer.updateBookCatalogStatus(bookId, "RETURN_BOOK");          //(4)

        return rental;
    }

}
