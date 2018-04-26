package uk.ac.man.cs.eventlite.controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;


@Controller
@RequestMapping(value = "/", produces = { MediaType.TEXT_HTML_VALUE })
public class HomeController {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public String setUpHomePage(Model model) {

		/* Finds upcoming 3 events */
		model.addAttribute("next3Events", eventService.findNext3Events());
		
		/* Find top 3 venues */		
	    Map <Venue, Integer> map = new HashMap<Venue, Integer>();
		
		for (Venue v : venueService.findAll()) {
			List<Event> venueEvents = v.getEvents();	
			map.put(v, venueEvents.size());
		}
		
		List<Map.Entry<Venue, Integer>> list = new LinkedList<Map.Entry<Venue,Integer>>(map.entrySet());

	    Collections.sort(list, new Comparator<Map.Entry<Venue,Integer>>() {
	    	
	        @Override
	        public int compare(Entry<Venue, Integer> e1, Entry<Venue, Integer> e2) {
	            return e2.getValue().compareTo(e1.getValue());
	        }		
	    });   

	    
		List<Venue> top3Venues = new LinkedList<Venue>();
		List<Integer> top3VenuesCount = new LinkedList<Integer>();
		
		int count = 3;
		if (list.size() < 3) {
			count = list.size();
		}
		
		for(int i = 0; i < count ; i++) {			
			top3Venues.add(i, list.get(i).getKey()); 
			top3VenuesCount.add(i, list.get(i).getValue()); 
		}

		model.addAttribute("top3Venues", top3Venues);
		model.addAttribute("top3VenuesCount", top3VenuesCount);
	    
		return "home/index";
	}
	
	
	
}
