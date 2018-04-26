package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
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
import org.springframework.web.context.WebApplicationContext;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.EventLite;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class HomeControllerTest {

	private MockMvc mvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@Mock
	private EventService eventService;

	@Mock
	private VenueService venueService;

	@InjectMocks
	private HomeController homeController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(homeController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	@Test
	public void getUpcoming3EventsWhenNoEvents() throws Exception {
		when(eventService.findNext3Events()).thenReturn(Collections.<Event> emptyList());
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("home/index")).andExpect(handler().methodName("setUpHomePage"));

		verify(eventService).findNext3Events();
		verify(venueService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void getUpcoming3EventsWithEvents() throws Exception {
		when(eventService.findNext3Events()).thenReturn(Collections.<Event> singletonList(event));
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("home/index")).andExpect(handler().methodName("setUpHomePage"));

		verify(eventService).findNext3Events();
		verify(venueService).findAll();
	}
	
	@Test
	public void getEventsWhenNoEvents() throws Exception {
		when(venue.getEvents()).thenReturn(Collections.<Event> emptyList());
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("home/index")).andExpect(handler().methodName("setUpHomePage"));

		verify(venue).getEvents();
		verify(venueService).findAll();
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void getEventsWithEvents() throws Exception {
		when(venue.getEvents()).thenReturn(Collections.<Event> singletonList(event));
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("home/index")).andExpect(handler().methodName("setUpHomePage"));

		verify(venue).getEvents();
		verify(venueService).findAll();
		verifyZeroInteractions(venue);
	}
	
}
