package jpa.performance.sequence.ex2;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Random;
import java.util.stream.IntStream;

import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import jpa.base.JPABaseJUnitSupport;

public class BookJPATest extends JPABaseJUnitSupport {

	@Test
	void shouldCreateValidBook() {
		
		IntStream.range(0, 2).forEach( i -> {
			try {
			EntityTransaction tx = em.getTransaction();
	
			BookJPA book = new BookJPA.BookJPABuilder().title("Pro JPA 2 in Java EE 8").description("This book from Mike Keith")
					.price(38.5F).isbn("978-1-4842-3419-8").nbOfPages(771).build();
	
			// Persists the book to the database
			tx.begin();
			MDC.put("transaction.id", "" + new Random().nextInt());
			em.persist(book);
			tx.commit();
			MDC.clear();
			assertNotNull(book.getId(), "ID should not be null");
			} catch (Exception e) {e.printStackTrace();}
		});

	}

}
