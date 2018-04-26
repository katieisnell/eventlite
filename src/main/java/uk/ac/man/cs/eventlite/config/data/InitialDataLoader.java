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

	@SuppressWarnings("deprecation")
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (eventService.count() > 0 && venueService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		}
		Venue VenueZ = new Venue();
		VenueZ.setName("Venue Z");
		VenueZ.setCapacity(50);
		VenueZ.setRoadName("53 Manchester Road");
		VenueZ.setPostcode("E15 3BD");
    venueService.save(VenueZ);

		Venue VenueA = new Venue();
		VenueA.setName("Venue A");
		VenueA.setCapacity(50);
		VenueA.setRoadName("23 Manchester Road");
		VenueA.setPostcode("E14 3BD");
		venueService.save(VenueA);

		Venue VenueB = new Venue();
		VenueB.setName("Venue B");
		VenueB.setCapacity(1000);
		VenueB.setRoadName("Highland Road");
		VenueB.setPostcode("S43 2EZ");
		venueService.save(VenueB);
		
		Venue VenueC = new Venue();
		VenueC.setName("Venue C");
		VenueC.setCapacity(10);
		VenueC.setRoadName("19 Acacia Avenue");
		VenueC.setPostcode("WA15 8QY");
		venueService.save(VenueC);
		
		

		//Set dates to year + 1900, month, day, hour, minute
		Event event1 = new Event();
		event1.setName("COMP23412 Showcase, group G");
		event1.setVenue(VenueA);
		event1.setDate(new Date(117, 10, 12, 11, 10));
		event1.setTime(new Date(117, 10, 12, 11, 10));
		event1.setDescription("best event");



		Event event2 = new Event();
		event2.setName("COMP23412 Showcase, group H");
		event2.setVenue(VenueA);
		event2.setDate(new Date(117, 10, 12, 11, 10));
		event2.setTime(new Date(105, 04, 1, 10, 10));
		event2.setDescription("not so good event");

		Event event3 = new Event();
		event3.setName("COMP23412 Showcase, group F");
		event3.setVenue(VenueB);
		event3.setDate(new Date(110, 03, 14, 5, 10));
		event3.setTime(new Date(110, 03, 14, 5, 10));
		event3.setDescription("amazing event");
		
		Event event4 = new Event();
		event4.setName("Adam");
		event4.setVenue(VenueB);
		event4.setDate(new Date(110, 03, 14, 5, 10));
		event4.setTime(new Date(110, 03, 14, 5, 10));
	
		eventService.save(event1);
		eventService.save(event2);
		eventService.save(event3);
		eventService.save(event4);


		
		/* Below is example data from Week 5: Specification by example */
		
		Event eventAlpha = new Event();
		eventAlpha.setName("Event Alpha");
		eventAlpha.setVenue(VenueC);
		Date alphaDate = new Date(118, 07, 11, 12, 30);
		eventAlpha.setDate(alphaDate); // (year + 1900, month, day, hour, minute)
		eventAlpha.setTime(alphaDate);
		
		Event eventBeta = new Event();
		eventBeta.setName("Event Beta");
		eventBeta.setVenue(VenueC);
		Date betaDate = new Date(118, 07, 11, 10, 00);
		eventBeta.setDate(betaDate); // (year + 1900, month, day, hour, minute)
		eventBeta.setTime(betaDate);
		
		Event eventApple = new Event();
		eventApple.setName("Event Apple");
		eventApple.setVenue(VenueC);
		Date appleDate = new Date(118, 07, 12, 00, 00);
		eventApple.setDate(appleDate); // (year + 1900, month, day, hour, minute)
		
		/* Week 6: Event to show "Upcoming 3 events" is working */
		Event eventExtra = new Event();
		eventExtra.setName("Event Extra");
		eventExtra.setVenue(VenueA);
		Date extraDate = new Date(118, 07, 13, 00, 00);
		eventExtra.setDate(extraDate); // (year + 1900, month, day, hour, minute)
		
		
		Event eventFormer = new Event();
		eventFormer.setName("Event Former");
		eventFormer.setVenue(VenueA);
		Date formerDate = new Date(117, 01, 11, 11, 00);
		eventFormer.setDate(formerDate); // (year + 1900, month, day, hour, minute)
		eventFormer.setTime(formerDate);
		
		Event eventPrevious = new Event();
		eventPrevious.setName("Event Previous");
		eventPrevious.setVenue(VenueA);
		Date previousDate = new Date(117, 01, 11, 18, 30);
		eventPrevious.setDate(previousDate); // (year + 1900, month, day, hour, minute)
		eventPrevious.setTime(previousDate);
		
		Event eventPast = new Event();
		eventPast.setName("Event Past");
		eventPast.setVenue(VenueB);
		Date pastDate = new Date(117, 01, 10, 17, 00);
		eventPast.setDate(pastDate); // (year + 1900, month, day, hour, minute)
		eventPast.setTime(pastDate);
		
		eventService.save(eventPast);
		eventService.save(eventPrevious);
		eventService.save(eventFormer);
		eventService.save(eventApple);
		eventService.save(eventBeta);
		eventService.save(eventAlpha);
		eventService.save(eventExtra);
	}
}
