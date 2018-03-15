package uk.ac.man.cs.eventlite.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.dao.VenueService;

@Controller
@RequestMapping(value = "/venue", produces = MediaType.TEXT_HTML_VALUE)
public class VenuesController {

  @Autowired
  private VenueService venueService;

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
}
