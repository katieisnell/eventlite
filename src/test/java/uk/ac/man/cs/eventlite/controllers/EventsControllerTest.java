package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.man.cs.eventlite.config.Security;
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
public class EventsControllerTest {

  private final static String BAD_ROLE = "USER";

	private MockMvc mvc;

	private static final String TOOLONG = "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111" +
			  				 "111111111111111111111111111111111111111111111111111111111111111111111111";
			  
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
	
	@Mock
	private Twitter twitter;

	@InjectMocks
	private EventsController eventsController;
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}
	
	@Test
	public void isAuthorizedForUser() {
		TwitterTemplate twitter = new TwitterTemplate("API_KEY", "API_SECRET", "ACCESS_TOKEN", "ACCESS_TOKEN_SECRET");
		assertTrue(twitter.isAuthorized());
	}

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event> emptyList());
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());
		
		TwitterTemplate twitter = new TwitterTemplate("API_KEY", "API_SECRET", "ACCESS_TOKEN", "ACCESS_TOKEN_SECRET");
		assertTrue(twitter.isAuthorized());
		
		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void getIndexWhenNoFutureEvents() throws Exception {
		when(eventService.findFutureEvents()).thenReturn(Collections.<Event> emptyList());
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findFutureEvents();
		verify(venueService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void getIndexWhenNoPastEvents() throws Exception {
		when(eventService.findPastEvents()).thenReturn(Collections.<Event> emptyList());
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findPastEvents();
		verify(venueService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event> singletonList(event));
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}

	@Test
	public void getNewEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("events/new"))
		.andExpect(handler().methodName("newEvent"));
	}

	@Test
	public void postEmptyEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "").accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
		.andExpect(view().name("events/new"))
		.andExpect(model().attributeHasFieldErrors("event", "name"))
		.andExpect(handler().methodName("createEvent"));

		verify(eventService, never()).save(event);
	}

	@Test
	public void postPastEvent() throws Exception {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add("name", "lecture");
		parameters.add("date", "2010-10-15");

		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.params(parameters).accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
		.andExpect(view().name("events/new"))
		.andExpect(model().attributeHasFieldErrors("event", "date"))
		.andExpect(handler().methodName("createEvent"));

		verify(eventService, never()).save(event);
	}

	@Test
	public void postEvent() throws Exception{
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);

		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add("name", "lecture");
		parameters.add("date", "2018-10-15");

		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.params(parameters).accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("createEvent")).andExpect(flash().attributeExists("ok_message"));

		verify(eventService).save(arg.capture());
		assertThat("lecture", equalTo(arg.getValue().getName()));
	}

	@Test
  public void deleteEvent() throws Exception {
    mvc.perform(MockMvcRequestBuilders.delete("/events/1").with(user("Rob").roles(Security.ADMIN_ROLE))
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .accept(MediaType.TEXT_HTML).with(csrf()))
              .andExpect(status().isFound()).andExpect(content().string(""))
              .andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors());
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
		assertEquals(arg.getValue().getId(), 1);
		assertEquals(arg.getValue().getName(), "EventName");
		assertEquals(arg.getValue().getDate(), new Date(119, 00, 01));
	}

	@Test
	public void updateEventLongName() throws Exception
	{
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", "1").param("name", TOOLONG)
				.param("date", "2019-01-01").param("time","10:30")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("events/update"))
		.andExpect(model().attributeHasFieldErrors("event", "name"))
		.andExpect(handler().methodName("updateSave"))
		.andExpect(flash().attributeCount(0));

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
		.andExpect(handler().methodName("updateSave"))
		.andExpect(flash().attributeCount(0));

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
		.andExpect(handler().methodName("updateSave"))
		.andExpect(flash().attributeCount(0));

		verify(eventService, never()).save(event);
	}

	@Test
	public void updateEventLongDescription() throws Exception
	{
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", "1").param("name", "EventName")
				.param("date", "2019-01-01").param("time","10:30").param("description", TOOLONG)
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("events/update"))
		.andExpect(model().attributeHasFieldErrors("event", "description"))
		.andExpect(handler().methodName("updateSave"))
		.andExpect(flash().attributeCount(0));

		verify(eventService, never()).save(event);
	}
	
	@Test
	public void getDetailedList() throws Exception {
		when(event.getId()).thenReturn(1L);
		when(eventService.findOne(event.getId())).thenReturn(event);

		mvc.perform(get("/events/event").param("ename", event.getId() + "").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/EventPage")).andExpect(handler().methodName("eventPage"));

		verify(eventService).findOne(event.getId());
	}
	
	@Test
	public void getIndexWithSpecificEventNames() throws Exception {
		String testString = "Adam";
		when(eventService.listEventsByName(testString)).thenReturn(Collections.<Event> singletonList(event));

		mvc.perform(get("/events/search").param("search", testString).accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/search")).andExpect(handler().methodName("searchEventByName"));

		verify(eventService).listEventsByName(testString);
		verifyZeroInteractions(event);
	}

    @Test
	public void getIndexWithFutureEvents() throws Exception {
		when(eventService.findFutureEvents()).thenReturn(Collections.<Event> singletonList(event));
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findFutureEvents();
		verify(venueService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void getIndexWithPastEvents() throws Exception {
		when(eventService.findPastEvents()).thenReturn(Collections.<Event> singletonList(event));
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findPastEvents();
		verify(venueService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
}
