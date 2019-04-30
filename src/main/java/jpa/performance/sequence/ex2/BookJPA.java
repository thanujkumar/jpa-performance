package jpa.performance.sequence.ex2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

//https://vladmihalcea.com/from-jpa-to-hibernates-legacy-and-enhanced-identifier-generators/

@Data
@Entity
@Builder
public class BookJPA {
	/*
	 * Here behavior is different in each case when using @SequenceGenerator with @GeneratedValue - Using only JPA api's and hibernate as implementor
	 *  select  bookjpa_sequence.NEXTVAL from dual; will increment by 30
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookjpa_sequence_generator")
	@SequenceGenerator (name="bookjpa_sequence_generator", sequenceName="bookjpa_sequence", allocationSize=30)
	private Long id;

	@NotNull
	private String title;

	@Digits(integer = 4, fraction = 2)
	private Float price;

	@Size(max = 2000)
	private String description;

	@Size(min = 10, max = 20)
	private String isbn;

	@Positive
	private Integer nbOfPages;
}