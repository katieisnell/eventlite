package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		model.addAttribute("futureEvents", eventService.findFutureEvents());
		model.addAttribute("pastEvents", eventService.findPastEvents());
		model.addAttribute("events", eventService.findAll());
		model.addAttribute("venues", venueService.findAll());

		return "events/index";
	}
	
	//Method for searching the database with an event name as a parameter
	//Only whole event names match as per the specification
	@RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchEventByName(@RequestParam(value = "search", required = false) String name, Model model) {
        model.addAttribute("search", eventService.listEventsByName(name));
        return "events/search";
    }

	

}
