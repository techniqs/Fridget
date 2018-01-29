package at.ac.tuwien.sepm.fridget.server.controllers;

import at.ac.tuwien.sepm.fridget.common.entities.BillShare;
import at.ac.tuwien.sepm.fridget.common.entities.Event;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.EventService;
import at.ac.tuwien.sepm.fridget.server.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Rest endpoint for events
 */
@RestController
@RequestMapping("/event")
public class EventController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Autoinjected server event service
     */
    @Autowired
    private EventService eventService;

    /**
     * Create an event with the specified data
     *
     * @param received the specified data
     * @return the created event, or an http failure message if something went wrong on the server
     */
    @RequestMapping(value = "/createEvent", method = RequestMethod.POST)
    public ResponseEntity<BillShare> createEvent(@RequestBody Event received) {
        LOG.info("called createEvent");
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(eventService.createEvent(received), HttpStatus.OK);
        } catch (InvalidArgumentException e) {
            response = new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (PersistenceException e) {
            response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Finds all events for the specified group
     *
     * @param id The id of the specified group
     * @return The group, or an http failure message if something went wrong on the server
     */
    @RequestMapping(value = "/readEventsForGroup{id}", method = RequestMethod.GET)
    public ResponseEntity readEventsForGroup(@PathVariable("id") int id) {
        LOG.info("called readEventsForGroup");
        ResponseEntity response = null;
        try {
            response = new ResponseEntity(eventService.findEvents(id), HttpStatus.OK);
        } catch (InvalidArgumentException e) {
            response = new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (PersistenceException e) {
            response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "/fetchRecentEvents", method = RequestMethod.GET)
    public List<Event> fetchRecentEvents(Authentication authentication) throws InvalidArgumentException, PersistenceException {
        LOG.info("called fetchRecentEvents");
        User user = AuthUtils.extractUser(authentication);
        return eventService.fetchRecentEvents(user, -1);
    }

    @RequestMapping(value = "/checkForUpdates", method = RequestMethod.GET)
    public List<Event> checkForUpdates(Authentication authentication) throws InvalidArgumentException, PersistenceException {
        LOG.info("called checkForUpdates");
        User user = AuthUtils.extractUser(authentication);
        return eventService.checkForUpdates(user);
    }

}
