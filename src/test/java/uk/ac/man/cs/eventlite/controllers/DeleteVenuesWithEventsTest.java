package uk.ac.man.cs.eventlite.controllers;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")
public class DeleteVenuesWithEventsTest {

  @Autowired
  private VenueService venueService;
  @Autowired
  private EventService eventService;
  
  @Test
  public void deleteVenueWithEvents() {
    try {
      Venue warehouse = new Venue();
      warehouse.setName("Warehouse One");
      warehouse.setCapacity(200);
      warehouse.setRoadName("23 Manchester Road");
      warehouse.setPostcode("E14 3BD");
      warehouse.setId(1);
      venueService.save(warehouse);
      Event event = new Event();
      event.setName("COMP23412 Showcase, group G");
      event.setVenue(warehouse);
      event.setDate(new Date(117, 10, 12, 11, 10));
      event.setTime(new Date(117, 10, 12, 11, 10));
      eventService.save(event);
      
      venueService.delete(1);
    }catch(DataIntegrityViolationException e ) {
      assertTrue("Warehouse One".equals(venueService.findById(1).getName()));
    }
  }
  

}
