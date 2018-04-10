package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
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
public class VenuesControllerTest {

	private MockMvc mvc;
	
	private String TOOLONG = "111111111111111111111111111111111111111111111111111111111111111111111111111111" + 
      "11111111111111111111111111111111111111111111111111111111111111111111111111111" +
      "11111111111111111111111111111111111111111111111111111111111111111111111111111" +
      "11111111111111111111111111111111111111111111111111111111111111111111111111111" +
      "11111111111111111111111111111111111111111111111111111111111111111111111111111" +
      "11111111111111111111111111111111111111111111111111111111111111111111111111111" +
      "11111111111111111111111111111111111111111111111111111111111111111111111111111";
	
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
	private VenuesController venuesController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}
	
	@Test
	public void getIndexWithAllVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService).findAll();
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService).findAll();
		verifyZeroInteractions(venue);
	}
	
	 @Test
	  public void deleteVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.delete("/venues/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	              .accept(MediaType.TEXT_HTML).with(csrf()))
	              .andExpect(status().isFound()).andExpect(content().string(""))
	              .andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors());
	  }
	 @Test
	  public void getNewVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.get("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .accept(MediaType.TEXT_HTML))
	    .andExpect(status().isOk()).andExpect(view().name("venues/new"))
	    .andExpect(handler().methodName("newVenue"));
	  }
	  
	  @Test
	  public void postEmptyNameVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "")
	        .param("postcode", "M1 4SX")
	        .param("capacity", "100")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/new"))
	    .andExpect(model().attributeHasFieldErrors("venue", "name"))
	    .andExpect(handler().methodName("createVenue"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void postEmptyPostcodeVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "university place")
	        .param("postcode", "")
	        .param("capacity", "10")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/new"))
	    .andExpect(model().attributeHasFieldErrors("venue", "postcode"))
	    .andExpect(handler().methodName("createVenue"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void postEmptyCapacityVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "university place")
	        .param("postcode", "M1 4SX")
	        .param("capacity", "")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/new"))
	    .andExpect(model().attributeHasFieldErrors("venue", "capacity"))
	    .andExpect(handler().methodName("createVenue"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void postEmptyRoadNameVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "university place")
	        .param("postcode", "M1 4SX")
	        .param("capacity", "5")
	        .param("roadName", "")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/new"))
	    .andExpect(model().attributeHasFieldErrors("venue", "roadName"))
	    .andExpect(handler().methodName("createVenue"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void postNegativeCapacityVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "university place")
	        .param("postcode", "M1 4SX")
	        .param("capacity", "-5")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/new"))
	    .andExpect(model().attributeHasFieldErrors("venue", "capacity"))
	    .andExpect(handler().methodName("createVenue"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void postLongerNameVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", TOOLONG)
	        .param("postcode", "M1 4SX")
	        .param("capacity", "12")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/new"))
	    .andExpect(model().attributeHasFieldErrors("venue", "name"))
	    .andExpect(handler().methodName("createVenue"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void postLongerRoadNameVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("roadName", TOOLONG)
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/new"))
	    .andExpect(model().attributeHasFieldErrors("venue", "roadName"))
	    .andExpect(handler().methodName("createVenue"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void postVenue() throws Exception{
	    ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
	    mvc.perform(MockMvcRequestBuilders.post("/venues").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name","university place")
	        .param("capacity","50")
	        .param("roadName","oxford road")
	        .param("postcode","M1 4SX")
	        .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(status().isFound()).andExpect(content().string(""))
	    .andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
	    .andExpect(handler().methodName("createVenue")).andExpect(flash().attributeExists("ok_message"));

	    verify(venueService).save(arg.capture());
	    assertThat("university place", equalTo(arg.getValue().getName()));
	  }
	  
	  @Test
	  public void getVenueToUp() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.get("/venues/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .accept(MediaType.TEXT_HTML))
	    .andExpect(status().isOk()).andExpect(view().name("venues/update"))
	    .andExpect(handler().methodName("updateR"));
	  }
	  
	  @Test
	  public void updateEmptyNameVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "")
	        .param("postcode", "M1 4SX")
	        .param("capacity", "100")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/update"))
	    .andExpect(model().attributeHasFieldErrors("venue", "name"))
	    .andExpect(handler().methodName("updateSave"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void updateEmptyPostcodeVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "university place")
	        .param("postcode", "")
	        .param("capacity", "10")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/update"))
	    .andExpect(model().attributeHasFieldErrors("venue", "postcode"))
	    .andExpect(handler().methodName("updateSave"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void updateEmptyCapacityVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "university place")
	        .param("postcode", "M1 4SX")
	        .param("capacity", "")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/update"))
	    .andExpect(model().attributeHasFieldErrors("venue", "capacity"))
	    .andExpect(handler().methodName("updateSave"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void updateEmptyRoadNameVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "university place")
	        .param("postcode", "M1 4SX")
	        .param("capacity", "5")
	        .param("roadName", "")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/update"))
	    .andExpect(model().attributeHasFieldErrors("venue", "roadName"))
	    .andExpect(handler().methodName("updateSave"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void updateNegativeCapacityVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", "university place")
	        .param("postcode", "M1 4SX")
	        .param("capacity", "-5")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/update"))
	    .andExpect(model().attributeHasFieldErrors("venue", "capacity"))
	    .andExpect(handler().methodName("updateSave"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void updateLongNameVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name", TOOLONG)
	        .param("postcode", "M1 4SX")
	        .param("capacity", "12")
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/update"))
	    .andExpect(model().attributeHasFieldErrors("venue", "name"))
	    .andExpect(handler().methodName("updateSave"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void updateLongRdNameVenue() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post("/venues/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("roadName", TOOLONG)
	        .accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
	    .andExpect(view().name("venues/update"))
	    .andExpect(model().attributeHasFieldErrors("venue", "roadName"))
	    .andExpect(handler().methodName("updateSave"));

	    verify(venueService, never()).save(venue);
	  }
	  
	  @Test
	  public void updateVenue() throws Exception{
	    ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
	    mvc.perform(MockMvcRequestBuilders.post("/venues/update/1").with(user("Rob").roles(Security.ADMIN_ROLE))
	        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        .param("name","university place")
	        .param("capacity","50")
	        .param("roadName","oxford road")
	        .param("postcode","M1 4SX")
	        .accept(MediaType.TEXT_HTML).with(csrf()))
	    .andExpect(status().isFound()).andExpect(content().string(""))
	    .andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
	    .andExpect(handler().methodName("updateSave")).andExpect(flash().attributeExists("ok_message"));

	    verify(venueService).save(arg.capture());
	    assertThat("university place", equalTo(arg.getValue().getName()));
	  }
	
}