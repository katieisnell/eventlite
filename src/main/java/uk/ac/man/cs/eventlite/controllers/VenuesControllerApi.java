package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenuesControllerApi {

	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public Resources<Resource<Venue>> getAllVenues() {

		return venueToResource(venueService.findAll());
	}
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<Venue> getVenue(@PathVariable("id") long id) {
    	return venueToResource(venueService.findById(id));
    }
    
	private Resource<Venue> venueToResource(Venue venue) 
    {	
		Link selfLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).withSelfRel();
		
		Link venueLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).withRel("venue");
    	
    	Link eventsLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).slash("events").withRel("events");
    	
    	Link next3EvLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).slash("next3events").withRel("next3events");

		return new Resource<Venue>(venue, selfLink, venueLink, eventsLink, next3EvLink);
	}

    @RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
    public Resources<Resource<Event>> getEvents(@PathVariable("id") long id) {
    	return eventToResource(venueService.findById(id).getEvents());
    }
    
    @RequestMapping(value = "/{id}/next3events", method = RequestMethod.GET)
    public Resources<Resource<Event>> get3Events(@PathVariable("id") long id) {
    	return event3ToResource(venueService.findById(id).getEvents());
    }
	
	private Resources<Resource<Venue>> venueToResource(Iterable<Venue> venues) {
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withSelfRel();
//		Link profileLink = linkTo(VenuesControllerApi.class).slash("profile").withRel("profile");

		List<Resource<Venue>> resources = new ArrayList<Resource<Venue>>();
		for (Venue venue : venues) {
			resources.add(venueToResource(venue));
		}

		return new Resources<Resource<Venue>>(resources, selfLink);//, profileLink);
	}
	
	private Resource<Event> eventToResource(Event event) {
		Link selfLink = linkTo(EventsControllerApi.class).slash(event.getId()).withSelfRel();

		return new Resource<Event>(event, selfLink);
	}
	
	private Resources<Resource<Event>> eventToResource(Iterable<Event> events) {
		Link selfLink = linkTo(methodOn(EventsControllerApi.class).getAllEvents()).withSelfRel();

		List<Resource<Event>> resources = new ArrayList<Resource<Event>>();
		for (Event event : events) {
			resources.add(eventToResource(event));
		}

		return new Resources<Resource<Event>>(resources, selfLink);
	}
	
	private Resources<Resource<Event>> event3ToResource(List<Event> events) {
		
		Collections.sort(events, new Comparator<Event>() {
			  public int compare(Event o1, Event o2) {
			      return o2.getDate().compareTo(o1.getDate());
			  }
			});
		
		Link selfLink = linkTo(methodOn(EventsControllerApi.class).getAllEvents()).withSelfRel();

		int index = 0;
		
		List<Resource<Event>> resources = new ArrayList<Resource<Event>>();
		
		for (Event event : events) {
			if(index == 3) break;
			resources.add(eventToResource(event));
			index++;
		}

		return new Resources<Resource<Event>>(resources, selfLink);
	}
}
