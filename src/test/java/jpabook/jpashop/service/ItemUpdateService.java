package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

@SpringBootTest
public class ItemUpdateService {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        // given (이런게 주어졌을 때)
        Book book = em.find(Book.class, 1L);

        //TX
        book.setName("asdfasdf");

        //변경 감지 == dirty checking

        // when (이렇게 하면)

        // then (이렇게 된다, 검증해라)
    }
}
