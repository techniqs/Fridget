package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.controllers.ApplicationController;
import at.ac.tuwien.sepm.fridget.client.util.RestClient;
import at.ac.tuwien.sepm.fridget.common.entities.Event;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.EventService;
import javafx.collections.FXCollections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ClientEventService implements EventService {

    private final List<Event> eventLog = FXCollections.observableArrayList();

    @Autowired
    RestClient restClient;

    @Autowired
    ApplicationController applicationController;

    @Override
    public Event createEvent(Event event) throws PersistenceException, InvalidArgumentException {
        try {
            Event received = restClient.postForObject(
                "/event/createEvent",
                event,
                Event.class);

            // Update event log
            applicationController.getList_view_log().getItems().add(0, received);// Add at the beginning

            return received;
        } catch (HttpServerErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new InvalidArgumentException(e.getMessage());
            } else if (e.getStatusCode().is5xxServerError()) {
                throw new PersistenceException(e.getMessage());
            }
        } catch (HttpClientErrorException e) {
            // Invalid credentials
        }
        return null;
    }

    @Override
    public List<Event> findEvents(int groupId) throws PersistenceException, InvalidArgumentException {
        ResponseEntity<Event[]> received = restClient.getForEntity("/event/readEventsForGroup{id}", Event[].class, groupId);

        if (received.getStatusCode().is4xxClientError()) {
            throw new InvalidArgumentException(received.toString());
        } else if (received.getStatusCode().is5xxServerError()) {
            throw new PersistenceException(received.toString());
        }
        return Arrays.asList(received.getBody());
    }

    @Override
    public List<Event> fetchRecentEvents(User user, int limit) throws PersistenceException, InvalidArgumentException {
        try {
            List<Event> rec = Arrays.asList(restClient.getForObject(
                "/event/fetchRecentEvents",
                Event[].class));

            applicationController.getList_view_log().getItems().clear();
            applicationController.getList_view_log().getItems().addAll(FXCollections.observableArrayList(rec));

            return rec;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public List<Event> checkForUpdates(User user) throws PersistenceException, InvalidArgumentException {
        ResponseEntity<Event[]> received = restClient.getForEntity("/event/checkForUpdates", Event[].class);

        if (received.getStatusCode().is4xxClientError()) {
            throw new InvalidArgumentException(received.toString());
        } else if (received.getStatusCode().is5xxServerError()) {
            throw new PersistenceException(received.toString());
        }
        List<Event> rec = Arrays.asList(received.getBody());
        Collections.reverse(rec);
        rec.forEach(element -> applicationController.getList_view_log().getItems().add(0, element));
        return rec;
    }

    @Override
    public List<Event> getEventLog() {
        return eventLog;
    }
}
