package uk.ac.man.cs.eventlite.dao;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired
	private EventRepository eventRepository;

	//private final static Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

	//private final static String DATA = "data/events.json";

	@Override
	public long count() {
		return eventRepository.count();
	}

	@Override
	public Iterable<Event> findAll() {
		return eventRepository.findByOrderByDateAscTimeAscNameAsc();
	}
	
	@Override
	public Iterable<Event> listEventsByName(String name) {
		return eventRepository.findByNameContainingIgnoreCaseOrderByDateAscNameAsc(name);
	}
	
	@Override
	public Iterable<Event> listEventsByNameUpcoming(String name) {
		return eventRepository.findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscNameAsc(name, new Date());
	}
	
	@Override
	public Iterable<Event> listEventsByNamePrevious(String name) {
		return eventRepository.findByNameContainingIgnoreCaseAndDateBeforeOrderByDateAscNameAsc(name, new Date());
	}
	
	@Override
	public Iterable<Event> findFutureEvents() {
		return eventRepository.findByDateAfterOrderByDateAscNameAsc(new Date());
	}
	
	@Override
	public Iterable<Event> findPastEvents() {
		return eventRepository.findByDateBeforeOrderByDateDescNameAsc(new Date());
	}
	
	@Override
	public void save(Event event) {
		eventRepository.save(event);
	}

    @Override
    public void delete(long id) {
        eventRepository.delete(id);
  }

  @Override
	public Event findById(long id) {
		return eventRepository.findById(id);
	}
	
	@Override
	public Event findOne(long event) {
		return eventRepository.findOne(event);
	}
	
	@Override
	public Iterable<Event> findNext3Events() {
		return eventRepository.findTop3ByDateAfterOrderByDateAscNameAsc(new Date());
	}
	
	@Override
	public long countByVenue(Venue venue) {
		return eventRepository.countByVenue(venue);
	}

}
