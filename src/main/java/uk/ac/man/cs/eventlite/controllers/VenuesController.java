package uk.ac.man.cs.eventlite.controllers;


import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping(value = "/venues", produces = MediaType.TEXT_HTML_VALUE)
public class VenuesController {

  @Autowired
  private VenueService venueService;
  
  @Autowired
  private EventService eventService;

  @RequestMapping(method = RequestMethod.GET)
  public String getAllVenues(Model model) {

    model.addAttribute("venues", venueService.findAll());

    return "venues/index";
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deletevenue(@PathVariable("id") long id, RedirectAttributes redirectAttrs) {
    try {
      venueService.delete(id);
    }catch(Exception e) {
      redirectAttrs.addFlashAttribute("error_message", "venue cannot be deleted.");
      return "redirect:/venues";
    }
    return "redirect:/venues";
  }
  
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newVenue(Model model) {
		if (!model.containsAttribute("venue")) {
			model.addAttribute("venue", new Venue());
		}

		return "venues/new";
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createVenue(@RequestBody @Valid @ModelAttribute Venue venue,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) throws ApiException, InterruptedException, IOException {

		if (errors.hasErrors()) {
			model.addAttribute("venue", venue);
			return "venues/new";
		}
		
		double[] latLong = getLatLong(venue.getPostcode());
		
		venue.setLatitude(latLong[0]);
		venue.setLongitude(latLong[1]);
		venueService.save(venue);
		redirectAttrs.addFlashAttribute("ok_message", "New venue added.");

		return "redirect:/venues";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	  public String updateR(@PathVariable("id") long id, Model model)
	  {
	    model.addAttribute("venue", venueService.findById(id));
	      return "venues/update";
	  }

	  @RequestMapping(value= "update/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	  public String updateSave(@PathVariable("id") long id,
	      @RequestBody @Valid @ModelAttribute Venue venue,
	            BindingResult errors,
	            Model model,
	            RedirectAttributes redirectAttrs) throws ApiException, InterruptedException, IOException
	  {
	    if (errors.hasErrors())
	    {
	      model.addAttribute("venues", venueService.findAll());
	            return "venues/update";
	    }
	    
	    double[] latLong = getLatLong(venue.getPostcode());
		
		venue.setLatitude(latLong[0]);
		venue.setLongitude(latLong[1]);

	    venueService.save(venue);

	    redirectAttrs.addFlashAttribute("ok_message", "Event succesfully updated");
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
  
  private double[] getLatLong(String address) throws ApiException, InterruptedException, IOException
  {
	  GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyAUk8QtlJRZ4_-o4YRacu59TUZuA6X1Qm8").build();
	  GeocodingResult[] results =  GeocodingApi.geocode(context, address).await();
		
		String[] latlong =  results[0].geometry.location.toString().split(","); 
		double[] latAndLong = {Double.parseDouble(latlong[0]),Double.parseDouble(latlong[1])};
		context.shutdown();
		return latAndLong;
  }
}
