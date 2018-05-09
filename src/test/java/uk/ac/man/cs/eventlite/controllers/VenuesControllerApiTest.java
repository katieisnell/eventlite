package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.man.cs.eventlite.testutil.MessageConverterUtil.getMessageConverters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class VenuesControllerApiTest {

	private MockMvc mvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private VenueService venueService;

	@Mock 
	private Venue venue;
	
	@InjectMocks
	private VenuesControllerApi venuesController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).apply(springSecurity(springSecurityFilterChain))
				.setMessageConverters(getMessageConverters()).build();
	}

	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")));

		verify(venueService).findAll();
	}

	@Test
	public void getIndexWithVenues() throws Exception {
		venue = new Venue();
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)))
				.andExpect(jsonPath("$._embedded.venues[0]._links.venue.href", endsWith("venues/0")))
				.andExpect(jsonPath("$._embedded.venues[0]._links.events.href", endsWith("venues/0/events")))
				.andExpect(jsonPath("$._embedded.venues[0]._links.next3events.href", endsWith("venues/0/next3events")));
		
		verify(venueService).findAll();
	}
	
	@Test
	public void getVenue() throws Exception {
		int id = 0;
		venue = new Venue();
		venue.setId(id);
		when(venueService.findById(id)).thenReturn(venue);

		mvc.perform(get("/api/venues/{id}", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getVenue")).andExpect(jsonPath("$.length()", equalTo(8)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/venues/"+ id)))
				.andExpect(jsonPath("$._links.venue.href", endsWith("/venues/"+ id)))
				.andExpect(jsonPath("$._links.events.href", endsWith("/venues/"+ id + "/events")))
				.andExpect(jsonPath("$._links.next3events.href", endsWith("/venues/"+ id + "/next3events")));
		
		verify(venueService).findById(id);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void getVenue3EventsPastEvent() throws Exception {
		int id = 0;
		Event event = new Event();
		event.setDate(new Date(117, 11, 1));
		event.setId(id);
		when(venueService.findById(id)).thenReturn(venue);
		when(venue.getEvents()).thenReturn(Collections.<Event> singletonList(event));

		mvc.perform(get("/api/venues/{id}/next3events", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("get3Events")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/next3events")));
		
		verify(venueService).findById(id);
	}
	
	@Test
	public void getVenue3EventsNoEvents() throws Exception {
		int id = 0;
		when(venueService.findById(id)).thenReturn(venue);
		when(venue.getEvents()).thenReturn(Collections.<Event> emptyList());

		mvc.perform(get("/api/venues/{id}/next3events", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("get3Events")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/next3events")));
		
		verify(venueService).findById(id);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void getVenue3EventsFutureEvent() throws Exception {
		int id = 0;
		Event event = new Event();
		event.setDate(new Date(119, 11, 1));
		event.setId(id);
		when(venueService.findById(id)).thenReturn(venue);
		when(venue.getEvents()).thenReturn(Collections.<Event> singletonList(event));

		mvc.perform(get("/api/venues/{id}/next3events", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("get3Events")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/next3events")));
		
		verify(venueService).findById(id);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void getVenue3EventsMoreThan3FutureEvents() throws Exception {
		int id = 0;
		Event event = new Event();
		event.setDate(new Date(119, 11, 1));
		event.setId(id);
		Event event1 = new Event();
		event1.setDate(new Date(119, 11, 1));
		event.setId(id+1);
		Event event2 = new Event();
		event2.setDate(new Date(119, 11, 1));
		event2.setId(id+2);
		Event event3 = new Event();
		event3.setDate(new Date(119, 11, 1));
		event3.setId(id+3);
		
		ArrayList<Event> events = new ArrayList<Event>();
		
		events.add(event);
		events.add(event1);
		events.add(event2);
		events.add(event3);
		
		when(venueService.findById(id)).thenReturn(venue);
		when(venue.getEvents()).thenReturn(events);

		mvc.perform(get("/api/venues/{id}/next3events", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("get3Events")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(3)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/next3events")));
		
		verify(venueService).findById(id);
	}
}
