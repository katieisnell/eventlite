package uk.ac.man.cs.eventlite.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "venues")
public class Venue {
	@Id
	@Column (name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column (name = "name")
	@Size(max = 256, message = "The name should have a maximum of 256 characters")
	@NotEmpty(message = "Venue name can not be empty")
	private String name;
  
	@Column (name = "capacity")
	@Min(value = 1, message = "The value must be positive")
	private int capacity;
	
	@Column (name = "address")
	private String address;
	
	@Column (name = "postcode")
	@NotEmpty(message = "Postcode can not be empty")
	@Pattern(regexp="^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z])))) [0-9][A-Za-z]{2})$",
	message="The postCode must be valid")
	private String postcode;
	
	@Column (name = "roadName")
	@NotEmpty(message = "Road name can not be empty")
	@Size(max = 300, message = "The road name should have a maximum of 300 characters")
	private String roadName;
	
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

	public void setAddress() {
	  this.address = getRoadName() + "," + getPostcode();
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
	
	public String getPostcode() {
    return postcode;
  }

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	
	public String getRoadName() {
    return roadName;
  }

	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
}
