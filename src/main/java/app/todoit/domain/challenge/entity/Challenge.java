package app.todoit.domain.challenge.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "challenge")
@Entity
public class Challenge {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "challenge_id")
	private Long id;

	private String title;
	private String content;
	private String day; //TODO json array (1) Converter (2)string (3) day entity
	private String off_day;
	private Date start_date;
	private Date end_date;
	private boolean status;

	@OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Challenger> challengers = new ArrayList<>();

	public void addChallenger(Challenger challenger){
		this.challengers.add(challenger);
		if(challenger.getChallenge() != this){
			challenger.setChallenge(this);
		}

	}
}
