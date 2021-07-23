package com.my.rental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.Objects;
import java.time.LocalDate;

/**
 * A ReturnedItem.
 */
@Entity
@Table(name = "returned_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@ToString
public class ReturnedItem implements Serializable {

    private static final long serialVersionUID = 1L;

    //반납아이템 일련번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //반납한 재고 도서 일련번호(도서 서비스에서 발행한 재고 도서 일련번호)
    @Column(name = "book_id")
    private Long bookId;

    //반납일자
    @Column(name = "returned_date")
    private LocalDate returnedDate;

    //반납 도서명
    @Column(name = "book_title")
    private String bookTitle;

    //반납아이템 생성 메서드
    public static ReturnedItem createReturnedItem(Long bookId, String bookTitle, LocalDate now) {
        ReturnedItem returnedItem = new ReturnedItem();
        returnedItem.setBookId(bookId);
        returnedItem.setBookTitle(bookTitle);
        returnedItem.setReturnedDate(now);
        return returnedItem;
    }

}
