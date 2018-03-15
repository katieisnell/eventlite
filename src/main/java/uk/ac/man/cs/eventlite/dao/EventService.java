package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public Iterable<Event> findFutureEvents();
	
	public Iterable<Event> findPastEvents();
	
	public void save(Event event);
	
	public Event findOne(long event);

	public Iterable<Event> listEventsByName(String name);
	
	public Iterable<Event> listEventsByNameUpcoming(String name);
	
	public Iterable<Event> listEventsByNamePrevious(String name);

}
