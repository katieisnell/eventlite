package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();
	
	public Venue save(Venue venue);
	
	public Venue findById(long id);
	
	public Venue findOne(long venue);

    public boolean delete(long id);
	
	public Iterable<Venue> listVenuesByName(String name);
	

}
