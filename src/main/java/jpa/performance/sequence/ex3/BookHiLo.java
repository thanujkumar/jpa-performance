package jpa.performance.sequence.ex3;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.Builder;
import lombok.Data;

//https://vladmihalcea.com/from-jpa-to-hibernates-legacy-and-enhanced-identifier-generators/
//https://vladmihalcea.com/the-hilo-algorithm/
@Data
@Entity
@Builder
public class BookHiLo {
	/*
	 *  Here behavior is different in each case when using @@GenericGenerator with @GeneratedValue - Using hibernate API's
	 *  select  bookhilo_sequence.NEXTVAL from dual; will increment by 30
	 *  select  bookhilo_sequence.CURRVAL from dual;
	 */
	//https://vladmihalcea.com/from-jpa-to-hibernates-legacy-and-enhanced-identifier-generators/
    //https://vladmihalcea.com/the-hilo-algorithm/

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookhilo_sequence_generator")
	@GenericGenerator(name = "bookhilo_sequence_generator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", 
	       parameters = {@Parameter(name="sequence_name", value="bookhilo_sequence"),
	    		         @Parameter(name="optimizer", value="hilo"),
	    		         @Parameter(name="initial_value", value="1"),
	    		         @Parameter(name="increment_size", value="3")})
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