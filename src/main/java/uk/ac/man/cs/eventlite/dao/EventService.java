package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public Iterable<Event> findFutureEvents();
	
	public Iterable<Event> findPastEvents();
	
	public Iterable<Event> findNext3Events();
	
	public void save(Event event);

    public void delete(long id);
    
    public Event findById(long id);
	
	public Event findOne(long event);

	public Iterable<Event> listEventsByName(String name);
	
	public Iterable<Event> listEventsByNameUpcoming(String name);
	
	public Iterable<Event> listEventsByNamePrevious(String name);
	
	public long countByVenue(Venue venue);

}
