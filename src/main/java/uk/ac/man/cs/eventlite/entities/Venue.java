package uk.ac.man.cs.eventlite.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "venues")
public class Venue {
	@Id
	@Column (name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column (name = "name")
	private String name;
  
	private int capacity;
	
	private String address;
	
	@OneToMany(mappedBy = "venue")
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

	public void setAddress(String addr) {
	  this.address = addr;
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
	public String getAddress() {
    return address;
  }

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
