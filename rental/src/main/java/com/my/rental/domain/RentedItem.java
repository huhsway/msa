package com.my.rental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A RentedItem.
 */
@Entity
@Table(name = "rented_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RentedItem implements Serializable {

    private static final long serialVersionUID = 1L;

    //대출아이템 일련번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //대출한 재고 도서 일련번호(도서 서비스에서 발행한 번호)
    @Column(name = "book_id")
    private Long bookId;

    //대출 시작일자
    @Column(name = "rented_date")
    private LocalDate rentedDate;

    //반납 예정일자
    @Column(name = "due_date")
    private LocalDate dueDate;

    //대출한 도서명
    @Column(name = "book_title")
    private String bookTitle;

    @ManyToOne
    @JsonIgnoreProperties(value = "rentedItems", allowSetters = true)
    private Rental rental;

    //대출 아이템을 생성하는 메서드
    public static RentedItem createRentedItem(Long bookId, String bookTitle, LocalDate rentedDate) {
        RentedItem rentedItem = new RentedItem();
        rentedItem.setBookId(bookId);
        rentedItem.setBookTitle(bookTitle);
        rentedItem.setRentedDate(rentedDate);
        rentedItem.setDueDate(rentedDate.plusWeeks(2));
        return rentedItem;

    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public RentedItem bookId(Long bookId) {
        this.bookId = bookId;
        return this;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public LocalDate getRentedDate() {
        return rentedDate;
    }

    public RentedItem rentedDate(LocalDate rentedDate) {
        this.rentedDate = rentedDate;
        return this;
    }

    public void setRentedDate(LocalDate rentedDate) {
        this.rentedDate = rentedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public RentedItem dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Rental getRental() {
        return rental;
    }

    public RentedItem rental(Rental rental) {
        this.rental = rental;
        return this;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }
// jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RentedItem)) {
            return false;
        }
        return id != null && id.equals(((RentedItem) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RentedItem{" +
            "id=" + getId() +
            ", bookId=" + getBookId() +
            ", rentedDate='" + getRentedDate() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            "}";
    }
}
