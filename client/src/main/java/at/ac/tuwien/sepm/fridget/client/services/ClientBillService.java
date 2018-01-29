package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.RestClient;
import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.BillService;
import at.ac.tuwien.sepm.fridget.common.services.EventService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Service
public class ClientBillService implements BillService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventService eventService;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Bill createBill(Bill bill, User user) throws PersistenceException, InvalidArgumentException {
        // Map property values to Bill object (bill is of type BillUIModel, which extends Bill)
        bill = modelMapper.map(bill, Bill.class);
        // Tag mapping doesn't work by default
        if (bill.getTag() != null && bill.getTag().getId() == 0 && bill.getTag().getName() == null) {
            bill.setTag(null);
        }

        try {
            return restClient.postForObject("/bill/createBill", bill, Bill.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public Bill editBill(Bill editedBill) throws InvalidArgumentException, PersistenceException {
        editedBill = modelMapper.map(editedBill, Bill.class);
        // Tag mapping doesn't work by default
        if (editedBill.getTag() != null && editedBill.getTag().getId() == 0 && editedBill.getTag().getName() == null) {
            editedBill.setTag(null);
        }

        // Get original bill and compare for event creation
        Bill original = findBillById(editedBill.getId());
        if (original.getTag() != null && original.getTag().getId() == 0 && original.getTag().getName() == null) {
            original.setTag(null);
        }

        List<String> changes = compareBills(original, editedBill);
        if (changes.size() > 0) {
            StringJoiner joiner = new StringJoiner(", ");
            changes.forEach(joiner::add);
            eventService.createEvent(new Event(
                EventType.MODIFY_BILL,
                false,
                LocalDateTime.now(),
                joiner.toString(),
                authenticationService.getUser(),
                null,
                original,
                editedBill,
                editedBill.getGroup()
            ));
        }

        try {
            return restClient.postForObject("/bill/editBill", editedBill, Bill.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public boolean deleteBill(int billID) throws InvalidArgumentException, PersistenceException {
        try {
            Bill bill = findBillById(billID);
            User user = authenticationService.getUser();
            boolean result = restClient.getForObject("/bill/deleteBill/{billID}", Boolean.class, billID);

            // Create event
            eventService.createEvent(new Event(
                EventType.MODIFY_BILL,
                false,
                LocalDateTime.now(),
                "Deleted bill " + bill.getTitle(),
                user,
                null,
                bill,
                bill,
                bill.getGroup()
            ));

            return result;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public boolean undoDeleteBill(int billID) throws InvalidArgumentException, PersistenceException {
        try {
            return restClient.getForObject("/bill/undoDeleteBill/{billID}", Boolean.class, billID);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public List<Bill> listAllBills(BillQuery billQuery) throws PersistenceException, InvalidArgumentException {
        try {
            return Arrays.asList(restClient.postForObject("/bill/listBills", billQuery, Bill[].class));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public List<Bill> searchBill(BillQuery query) {
        return null;
    }

    @Override
    public Bill findBillById(int billId) throws PersistenceException, InvalidArgumentException {
        if (billId < 1) throw new InvalidArgumentException("[Bill service] Bill ID invalid");
        try {
            return restClient.postForObject("/bill/findBillById", billId, Bill.class);
        } catch (HttpServerErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new InvalidArgumentException(e.getMessage());
            } else if (e.getStatusCode().is5xxServerError()) {
                throw new PersistenceException(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public boolean validateBill(Bill bill) throws InvalidArgumentException {
        return false;
    }

    private List<String> compareBills(Bill original, Bill other) {
        List<String> changes = new ArrayList<>();

        // Check split option
        if (original.getShareTechniqueId().getId() != other.getShareTechniqueId().getId()) {
            changes.add("New share technique: " + other.getShareTechniqueId().name());
        }
        if (!original.getTitle().equals(other.getTitle())) {
            changes.add("New title: " + other.getTitle());
        }
        if (original.getDescription()!= null && !original.getDescription().equals(other.getDescription())) {
            changes.add("New description: " + other.getDescription());
        }
        if (original.getBillShares().size() != other.getBillShares().size()) {
            // Removed users
            StringBuilder removed = new StringBuilder();
            for (BillShare share : original.getBillShares()) {
                if (original.getBillShares().contains(share) && !other.getBillShares().contains(share))
                    removed.append(share.getUser().getName() + ", ");
            }
            // Added users
            StringBuilder added = new StringBuilder();
            for (BillShare otherShare : other.getBillShares()) {
                if (!original.getBillShares().contains(otherShare) && other.getBillShares().contains(otherShare))
                    added.append(other.getUser().getName() + ", ");
            }
            if (removed.length() != 0)
                changes.add("Removed users: " + removed.toString());
            if (added.length() != 0)
                changes.add("Added users: " + added.toString());
        }
        // If user deleted bill discard all other changes
        if (!original.isDeleted() && other.isDeleted()) {
            changes.clear();
            changes.add("Deleted bill"); // True if user deleted false if user undeleted
        } else if (original.isDeleted() && !other.isDeleted()) {
            changes.add("Undeleted bill");
        }
        // Tags
        if (original.getTag() != null && other.getTag() == null) {
            changes.add("Removed tag " + original.getTag().getName());
        } else if (original.getTag() == null && other.getTag() != null) {
            changes.add("Added tag " + other.getTag().getName());
        } else if (original.getTag() != null && other.getTag() != null
            && !original.getTag().equals(other.getTag())) {
            changes.add("Changed tag to " + other.getTag().getName());
        }

        return changes;
    }
}
