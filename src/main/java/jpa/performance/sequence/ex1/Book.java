package jpa.performance.sequence.ex1;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
public class Book {

	@Id
	@GeneratedValue
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