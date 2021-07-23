package com.my.rental.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

import com.my.rental.domain.enumeration.RentalStatus;

/**
 * Rental 애그리거트 루트, 엔티티 클래
 */
@Entity
@Table(name = "rental")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Rental implements Serializable {

    private static final long serialVersionUID = 1L;
    //Rental 일련번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //사용자 일련번호
    @Column(name = "user_id")
    private Long userId;

    //대출 가능 여부
    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status")
    private RentalStatus rentalStatus;

    //연체료
    @Column(name = "late_fee")
    private int lateFee;

    //대출아이템
    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true) //고아 객체 제거 -> rental에서 컬렉션의 객체 삭제시, 해당 컬렉션의 entity삭제
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<RentedItem> rentedItems = new HashSet<>();

    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OverdueItem> overdueItems = new HashSet<>();

    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ReturnedItem> returnedItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public Rental userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RentalStatus getRentalStatus() {
        return rentalStatus;
    }

    public Rental rentalStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
        return this;
    }

    public void setRentalStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rental)) {
            return false;
        }
        return id != null && id.equals(((Rental) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Rental{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", rentalStatus='" + getRentalStatus() + "'" +
            "}";
    }
    //최초 대출 시//
    /**
     * 대출카드 생성
     *
     * @param userId
     * @return
     */
    public static Rental createRental(Long userId) {
        Rental rental = new Rental();
        rental.setUserId(userId);
        //대출 가능하게 상태 변경
        rental.setRentalStatus(RentalStatus.RENT_AVAILABLE);
        rental.setLateFee(0);
        return rental;
    }

    /**
     * 대출하기
     *
     * @param bookid
     * @param title
     * @return
     */
    public Rental rentBook(Long bookid, String title) {
        this.addRentedItem(RentedItem.createRentedItem(bookid, title, LocalDate.now()));
        return this;
    }

    /**
     * 반납하기
     *
     * @param bookId
     * @return
     */
    public Rental returnbook(Long bookId) {
        RentedItem rentedItem = this.rentedItems.stream().filter(item -> item.getBookId().equals(bookId)).findFirst().get();
        this.addReturnedItem(ReturnedItem.createReturnedItem(rentedItem.getBookId(), rentedItem.getBookTitle(), LocalDate.now()));
        this.removeRentedItem(rentedItem);
        return this;
    }

    //대출 가능 여부 체크 //
    public void checkRentalAvailable() throws RentUnavailableException {
        if(this.rentalStatus.equals(RentalStatus.RENT_UNAVAILABLE ) || this.getLateFee()!=0) throw new RentUnavailableException("연체 상태입니다. 연체료를 정산 후, 도서를 대출하실 수 있습니다.");
        if(this.getRentedItems().size()>=5) throw new RentUnavailableException("대출 가능한 도서의 수는 "+( 5- this.getRentedItems().size())+"권 입니다.");

    }
}
