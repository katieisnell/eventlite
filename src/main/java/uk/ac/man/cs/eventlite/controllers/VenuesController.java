package uk.ac.man.cs.eventlite.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;

@Controller
@RequestMapping(value = "/venues", produces = MediaType.TEXT_HTML_VALUE)
public class VenuesController {

  @Autowired
  private VenueService venueService;
  
  @Autowired
  private EventService eventService;

  @RequestMapping(method = RequestMethod.GET)
  public String getAllVenus(Model model) {

    model.addAttribute("venues", venueService.findAll());

    return "venues/index";
  }


  
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deletevenue(@PathVariable("id") long id) {
    venueService.delete(id);
      return "redirect:/venues";
  }
  

	@RequestMapping(value="/venue", method = RequestMethod.GET)
	public String venuePage(Model model, @RequestParam("vname") long vname) {
		model.addAttribute("venue", venueService.findOne(vname));
		model.addAttribute("events", venueService.findOne(vname).getEvents());
		return "venues/VenuePage";
	}
	
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public String searchVenueByName(@RequestParam(value = "search", required = false) String name, Model model) {
      model.addAttribute("search", venueService.listVenuesByName(name));
      return "venues/search";
	}
}
