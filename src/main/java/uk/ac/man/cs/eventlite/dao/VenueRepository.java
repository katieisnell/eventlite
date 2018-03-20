package uk.ac.man.cs.eventlite.dao;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueRepository extends CrudRepository<Venue, Long> {
	
	Optional<Venue> findById(long id);
	Iterable<Venue> findByOrderByNameAsc();
	
}
