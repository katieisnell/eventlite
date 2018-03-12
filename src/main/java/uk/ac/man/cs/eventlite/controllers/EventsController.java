package uk.ac.man.cs.eventlite.controllers;

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

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		model.addAttribute("events", eventService.findAll());

		return "events/index";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newEvent(Model model,@ModelAttribute("event")Event event) {
		if (!model.containsAttribute("events")) {
			model.addAttribute("events", new Event());
		}

		model.addAttribute("venues", venueService.findAll());

		return "events/new";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Valid @ModelAttribute Event event,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("events", event);
			model.addAttribute("venues", venueService.findAll());
			return "events/new";
		}

		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New event added.");

		return "redirect:/events";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String deleteEvent(@PathVariable("id") long id) {

	    eventService.delete(id);
	    return "redirect:/events";
	}

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public String updateR(@PathVariable("id") long id, Model model)
  {
    model.addAttribute("event", eventService.findById(id));
    model.addAttribute("venues", venueService.findAll());
      return "events/update";
  }

  @RequestMapping(value= "update/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String updateSave(@PathVariable("id") long id,
      @RequestBody @Valid @ModelAttribute Event event,
            BindingResult errors,
            Model model,
            RedirectAttributes redirectAttrs)
  {
    if (errors.hasErrors())
    {
      model.addAttribute("venues", venueService.findAll());
            return "events/update";
        }

        eventService.save(event);

        redirectAttrs.addFlashAttribute("ok_message", "Event succesfully updated");
        return "redirect:/events";
  }

}
