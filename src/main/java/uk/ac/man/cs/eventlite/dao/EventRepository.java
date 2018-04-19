package uk.ac.man.cs.eventlite.dao;


import java.util.Date;

import org.springframework.data.repository.CrudRepository;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventRepository extends CrudRepository<Event, Long>{


	Iterable<Event> findByOrderByDateAscTimeAscNameAsc();
	
	Iterable<Event> findByNameContainingIgnoreCaseOrderByDateAscNameAsc(String name);

	Iterable<Event> findByOrderByDateAscTimeAsc();
	
	Iterable<Event> findTop3ByDateAfterOrderByDateAscNameAsc(Date date);
	

	Event findById(long id);

	Iterable<Event> findByDateAfterOrderByDateAscNameAsc(Date date);
	
	Iterable<Event> findByDateBeforeOrderByDateDescNameAsc(Date date);

	Iterable<Event> findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscNameAsc(String name, Date date);
	
	Iterable<Event> findByNameContainingIgnoreCaseAndDateBeforeOrderByDateAscNameAsc(String name, Date date);
	
	long countByVenue(Venue venue);

}
