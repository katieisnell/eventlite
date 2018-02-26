package uk.ac.man.cs.eventlite.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "venues")
public class Venue {
	@Id
	@GeneratedValue
	private long id;

	private String name;

	private int capacity;
	
	@OneToMany
	private List<Event> events;
	
	public List<Event> getEvents() {
		return events;
	}

	public Venue() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
