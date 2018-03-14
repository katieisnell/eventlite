package uk.ac.man.cs.eventlite.dao;


import java.util.Optional;
import java.util.Date;

import org.springframework.data.repository.CrudRepository;
import uk.ac.man.cs.eventlite.entities.Event;

public interface EventRepository extends CrudRepository<Event, Long>{


	Iterable<Event> findByOrderByDateAscTimeAscNameAsc();
	
	Iterable<Event> findByNameContainingIgnoreCaseOrderByDateAscNameAsc(String name);

	Iterable<Event> findByOrderByDateAscTimeAsc();
	

	Optional<Event> findById(long id);

	Iterable<Event> findByDateAfterOrderByDateAscNameAsc(Date date);
	
	Iterable<Event> findByDateBeforeOrderByDateDescNameAsc(Date date);


}
