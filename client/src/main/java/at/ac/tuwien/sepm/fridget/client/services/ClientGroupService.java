package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.RestClient;
import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.BalanceNotZeroException;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.EventService;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import at.ac.tuwien.sepm.fridget.common.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Service
public class ClientGroupService implements GroupService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private GroupService groupService;

    @Override
    public Group createGroup(Group group, int userId) throws PersistenceException, InvalidArgumentException {
        group = modelMapper.map(group, Group.class);
        try {
            return restClient.postForObject("/group/createGroup", group, Group.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public boolean editGroup(Group group, int userId) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject("/group/editGroup", group, Boolean.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public List<Group> getGroups(int userId) throws PersistenceException {
        try {
            return Arrays.asList(restClient.getForObject(
                "/group/getGroups",
                Group[].class
            ));
        } catch (HttpClientErrorException e) {
            throw new PersistenceException(e.getResponseBodyAsString(), e);
        }
    }


    @Override
    public List<User> getUsersForGroup(int groupId, int userId) throws PersistenceException, InvalidArgumentException {
        try {
            return Arrays.asList(restClient.getForObject(
                "/group/{groupId}/getUsersForGroup",
                User[].class,
                groupId));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public Group getGroup(int groupId) throws PersistenceException, InvalidArgumentException {
        try {
            Group received = restClient.postForObject(
                "/group/getGroup",
                groupId,
                Group.class);
            int a = 2;
            return received;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public int inviteUserToGroup(int groupId, int memberId, String inviteeEmail) throws PersistenceException, InvalidArgumentException {
        try {
            UserInvitation userInvitation = new UserInvitation(inviteeEmail, groupId);

            int received = restClient.postForObject("/group/inviteUserToGroup", userInvitation, Integer.class);

            // Create event
            User invitee = userService.findUserByEmail(inviteeEmail);
            Group group = groupService.getGroup(groupId);
            eventService.createEvent(new Event(EventType.INVITATION_SENT, false, LocalDateTime.now(), null,
                authenticationService.getUser(), invitee, null, null, group));

            return received;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public boolean leaveGroup(int groupId, int userId) throws PersistenceException, BalanceNotZeroException, InvalidArgumentException {
        try {
            Group group = new Group(groupId);
            boolean received = restClient.postForObject("/group/leaveGroup", group, Boolean.class);

            // Create event for notification log
            User leaver = userService.findUserById(userId);
            Group leftGroup = groupService.getGroup(groupId);
            eventService.createEvent(
                new Event(EventType.MEMBER_LEFT,
                    false,
                    LocalDateTime.now(),
                    leaver.getName() + " left group " + leftGroup.getName(),
                    leaver,
                    null,
                    null,
                    null,
                    group)
            );

            return received;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new BalanceNotZeroException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public void acceptInvitation(int invitationId, int userId) throws PersistenceException, InvalidArgumentException {
        try {
            restClient.postForObject(
                "/group/acceptInvitation", invitationId, String.class);// String is okay to be null
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public void rejectInvitation(int invitationId, int userId) throws PersistenceException, InvalidArgumentException {
        try {
            restClient.postForObject(
                "/group/rejectInvitation", invitationId, String.class, invitationId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }


    @Override
    public boolean hasMember(int groupId, int userId) throws PersistenceException {
        return false;
    }

    @Override
    public List<Group> getGroupsWithMembersForUser(int userId) throws PersistenceException {
        return null;
    }

    @Override
    public List<GroupInvitation> getPendingInvitations(int userId) throws PersistenceException, InvalidArgumentException {
        try {
            return Arrays.asList(restClient.getForObject(
                "/group/getPendingInvitations",
                GroupInvitation[].class));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public void sendDebtInfoMail(int groupId, User user) throws InvalidArgumentException, PersistenceException {
        try {
            restClient.postForObject(
                "/group/sendDebtInfoMail", groupId, String.class);// String is okay to be null
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

}
