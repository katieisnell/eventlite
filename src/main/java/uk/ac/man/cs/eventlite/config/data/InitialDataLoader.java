package uk.ac.man.cs.eventlite.config.data;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (eventService.count() > 0 && venueService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		}

		Event event1 = new Event();
		event1.setName("COMP23412 Showcase, group G");
		event1.setVenue(1);
		event1.setDate(new Date());
		event1.setTime(new Date());


		Event event2 = new Event();
		event2.setName("COMP23412 Showcase, group H");
		event2.setVenue(1);
		event2.setDate(new Date());
		event2.setTime(new Date());

		Event event3 = new Event();
		event3.setName("COMP23412 Showcase, group F");
		event3.setVenue(1);
		event3.setDate(new Date());
		event3.setTime(new Date());

		eventService.save(event1);
		eventService.save(event2);
		eventService.save(event3);

		// Build and save initial models here.
		if (venueService.count() <= 0) {
			Venue kilburn = new Venue();
			kilburn.setName("Kilburn Building");
			kilburn.setCapacity(200);
			venueService.save(kilburn);
		}
	}
}
