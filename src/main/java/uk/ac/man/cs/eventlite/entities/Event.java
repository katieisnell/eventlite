package uk.ac.man.cs.eventlite.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "events")
public class Event {
	@Id
	@GeneratedValue
	private long id;
	
	@NotNull(message = "There must be a date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Future
	private Date date;
	
	@DateTimeFormat(pattern = "HH:mm")
	@Temporal(TemporalType.TIME)
	private Date time;

	@NotNull(message = "Should not be null")
	@NotEmpty(message = "Should not be empty")
	@Size(max = 256, message = "The name should have a maximum of 256 characters")
	private String name;

	@ManyToOne
	private Venue venue;
	
	@Size(max = 500, message = "The description should have a maximum of 500 characters")
	private String description;
		
	public Event() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
