package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Date;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class EventsControllerTest 
{
	private final static String BAD_ROLE = "USER";
	
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
	private EventsController eventsController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}
	
	@Test
	public void getEvent() throws Exception {
		when(eventService.findById(1)).thenReturn(event);

		mvc.perform(MockMvcRequestBuilders.get("/events/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/update")).andExpect(handler().methodName("updateR"));

		verify(eventService, times(1)).findById(1);
		verify(venueService, times(1)).findAll();
	}
	
	@Test
	public void getEventDetailsToUpdate() throws Exception 
	{
		Date toReturnDate = new Date();
		when(event.getName()).thenReturn("EventName");
		when(event.getId()).thenReturn((long)1);
		when(event.getDate()).thenReturn(toReturnDate);
		when(event.getTime()).thenReturn(toReturnDate);
		when(event.getVenue()).thenReturn(venue);
		when(event.getDescription()).thenReturn("Description");
		
		mvc.perform(MockMvcRequestBuilders.get("/events/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/update")).andExpect(handler().methodName("updateR"));

		assertEquals("EventName", event.getName());
		assertEquals((long)1, event.getId());
		assertEquals(toReturnDate, event.getDate());
		assertEquals(toReturnDate, event.getTime());
		assertEquals(venue, event.getVenue());
		assertEquals("Description", event.getDescription());
	}
	
	@Test
	public void updateEventNoAuth() throws Exception 
	{
		mvc.perform(MockMvcRequestBuilders.post("/events/1").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
		.andExpect(header().string("Location", endsWith("/sign-in")));

		verify(eventService, never()).save(event);
	}
	
	@Test
	public void updateEventNoCsrf() throws Exception 
	{
		mvc.perform(MockMvcRequestBuilders.post("/events/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)).andExpect(status().isForbidden());

		verify(eventService, never()).save(event);
	}
	
	@Test
	public void updateEventBadRole() throws Exception 
	{
		mvc.perform(MockMvcRequestBuilders.post("/events/1").with(user("Rob").roles(BAD_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isForbidden());

		verify(eventService, never()).save(event);
	}
	
	@Test
	public void updateEventTest() throws Exception 
	{	
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", "1")
				.param("name", "EventName").param("date", "2019-01-01")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateSave")).andExpect(flash().attributeExists("ok_message"));

		verify(eventService).save(arg.capture());
	}
	
	@Test
	public void updateEventLongName() throws Exception 
	{	
		String longName = "111111111111111111111111111111111111111111111111111111111111111111111111" + 
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111";
		
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", "1").param("name", longName)
				.param("date", "2019-01-01").param("time","10:30")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("events/update"))
		.andExpect(model().attributeHasFieldErrors("event", "name"))
		.andExpect(handler().methodName("updateSave"));

		verify(eventService, never()).save(event);
	}
	
	@Test
	public void updateEventNoName() throws Exception 
	{	
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", "1").param("name", "")
				.param("date", "2019-01-01").param("description", "Description").param("time","10:30")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("events/update"))
		.andExpect(model().attributeHasFieldErrors("event", "name"))
		.andExpect(handler().methodName("updateSave"));

		verify(eventService, never()).save(event);
	}
	
	
	@Test
	public void updatePastDate() throws Exception 
	{	
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", "1").param("name", "EventName")
				.param("date", "2015-01-01")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("events/update"))
		.andExpect(model().attributeHasFieldErrors("event", "date"))
		.andExpect(handler().methodName("updateSave"));

		verify(eventService, never()).save(event);
	}
	
	@Test
	public void updateEventLongDescription() throws Exception 
	{	
		String longDescription = "111111111111111111111111111111111111111111111111111111111111111111111111" + 
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111" +
						  "111111111111111111111111111111111111111111111111111111111111111111111111";
		
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", "1").param("name", longDescription)
				.param("date", "2019-01-01").param("time","10:30").param("description", longDescription)
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("events/update"))
		.andExpect(model().attributeHasFieldErrors("event", "description"))
		.andExpect(handler().methodName("updateSave"));

		verify(eventService, never()).save(event);
	}
	
}
