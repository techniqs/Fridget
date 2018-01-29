package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.*;
import at.ac.tuwien.sepm.fridget.common.util.Currency;
import at.ac.tuwien.sepm.fridget.server.persistence.GroupDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.UserDAO;
import at.ac.tuwien.sepm.fridget.server.util.Email;
import at.ac.tuwien.sepm.fridget.server.util.EmailContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ServerGroupService implements GroupService {

    @Autowired
    GroupDAO groupDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    BillService billService;
    @Autowired
    BillShareService billShareService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    EmailService emailService;
    @Autowired
    EventService eventService;
    @Autowired
    UserService userService;

    @Override
    public Group createGroup(Group group, int userId) throws InvalidArgumentException, PersistenceException {
        validateGroup(group);
        group = groupDAO.createGroup(group);
        int inviteId = groupDAO.inviteUserToGroup(group.getId(), userId);
        groupDAO.acceptInvitation(inviteId);
        group.setMembers(groupDAO.getUserIdsOfGroup(group.getId()));

        return group;
    }

    @Override
    public boolean editGroup(Group group, int userId) throws InvalidArgumentException, PersistenceException {
        if (!hasMember(group.getId(), userId)) {
            throw new InvalidArgumentException("The user is not a member of the group");
        }
        validateGroup(group);
        groupDAO.editGroup(group.getId(), group.getName());

        return true;
    }


    /**
     * Group validation
     *
     * @param group the group to validate
     * @throws InvalidArgumentException otherwise. Message contains the invalid fields
     */
    private void validateGroup(Group group) throws InvalidArgumentException {
        if (group == null) {
            throw new InvalidArgumentException("Group must not be null!");
        }
        StringBuilder message = new StringBuilder();
        if (group.getName() == null || group.getName().isEmpty()) {
            message.append("Name must not be empty.");
        } else if (group.getBaseCurrencyID() < 1) {
            message.append("Base currency must be set!");
        }

        if (!message.toString().isEmpty()) {
            throw new InvalidArgumentException(message.toString());
        }
    }


    @Override
    public int inviteUserToGroup(int groupId, int memberId, String inviteeEmail) throws PersistenceException, InvalidArgumentException {
        // Check if inviting user is member of group
        if (!hasMember(groupId, memberId)) {
            throw new InvalidArgumentException("The user is not a member of the group");
        }

        // Check if invitee is a valid user
        User invitee = userDAO.findUserByEmail(inviteeEmail);
        if (invitee == null) {
            throw new InvalidArgumentException("Invitee not found!");
        }


        // Check if invitee is already a group member
        int inviteeId = invitee.getId();
        if (hasMember(groupId, inviteeId)) {
            throw new InvalidArgumentException("The invitee is already a member of the group");
        }

        // Check if invitee has already been invited
        List<GroupInvitation> invitations = getPendingInvitations(inviteeId);
        for (GroupInvitation invitation : invitations) {
            if (invitation.getGroup().getId() == groupId) {
                throw new InvalidArgumentException("The invitee has already been invited");
            }
        }

        // Invite user to group
        int invitationId = groupDAO.inviteUserToGroup(groupId, inviteeId);

        // Send email notification to group members
        emailService.send(EmailContent.generateInviteMemberToGroupEmail(
            getUsersForGroup(groupId, memberId),
            invitee.getName(),
            groupDAO.getGroup(groupId).getName())
        );

        return invitationId;
    }

    @Override
    public void acceptInvitation(int invitationId, int userId) throws PersistenceException, InvalidArgumentException {
        List<GroupInvitation> invitations = getPendingInvitations(userId);
        for (GroupInvitation invitation : invitations) {
            if (invitation.getId() == invitationId) {
                groupDAO.acceptInvitation(invitationId);

                // Create event
                eventService.createEvent(new Event(
                    EventType.INVITATION_ACCEPTED,
                    false,
                    LocalDateTime.now(),
                    invitation.getUser().getName() + " was invited to group " + invitation.getGroup().getName(),
                    null,
                    invitation.getUser(),
                    null,
                    null,
                    invitation.getGroup()
                ));
                return;
            }
        }

        throw new InvalidArgumentException("The invitation does not belong to the given user");
    }

    @Override
    public void rejectInvitation(int invitationId, int userId) throws PersistenceException, InvalidArgumentException {
        List<GroupInvitation> invitations = getPendingInvitations(userId);
        for (GroupInvitation invitation : invitations) {
            if (invitation.getId() == invitationId) {
                groupDAO.rejectInvitation(invitationId);
                return;
            }
        }

        throw new InvalidArgumentException("The invitation does not belong to the given user");
    }

    @Override
    public boolean leaveGroup(int groupId, int userId) throws PersistenceException, InvalidArgumentException {
        if (!hasMember(groupId, userId)) {
            throw new InvalidArgumentException("The user is not a member of the group");
        }

        BigDecimal balance = transactionService.getBalance(groupId, userId);
        if (!balance.equals(BigDecimal.ZERO)) {
            throw new InvalidArgumentException("Balance is not 0. Balance equals " + balance);
        }

        groupDAO.leaveGroup(groupId, userId);
        groupDAO.rejectAllPendingInvitations(groupId);


        return true;
    }


    @Override
    public boolean hasMember(int groupId, int userId) throws PersistenceException {
        List<Integer> memberIds = groupDAO.getUserIdsOfGroup(groupId);
        return memberIds.contains(userId);
    }

    @Override
    public List<Group> getGroupsWithMembersForUser(int userId) throws PersistenceException {
        return groupDAO.getGroupsWithMembersForUser(userId);
    }


    @Override
    public List<GroupInvitation> getPendingInvitations(int userId) throws PersistenceException, InvalidArgumentException {
        return groupDAO.getPendingInvitations(userId);
    }

    @Override
    public List<Group> getGroups(int userId) throws PersistenceException {
        return groupDAO.getGroups(userId);
    }

    @Override
    public List<User> getUsersForGroup(int groupId, int userId) throws PersistenceException, InvalidArgumentException {
        if (!hasMember(groupId, userId)) {
            throw new InvalidArgumentException("The user is not a member of the group");
        }
        List<Integer> usersIds = groupDAO.getUserIdsOfGroup(groupId);
        List<User> users = new ArrayList<>();
        for (int id : usersIds) {
            users.add(userDAO.findUserById(id));
        }
        return users;
    }

    @Override
    public Group getGroup(int groupId) throws PersistenceException, InvalidArgumentException {
        if (groupId < 0)
            throw new InvalidArgumentException("Invalid value for group ID");
        return groupDAO.getGroup(groupId);
    }

    @Override
    public void sendDebtInfoMail(int groupId, User user) throws InvalidArgumentException, PersistenceException {
        Group group = getGroup(groupId);
        String currencySymbol = Currency.fromId(group.getBaseCurrencyID()).getSymbol();
        Map<Integer, BigDecimal> balances = transactionService.getBalanceToMembers(groupId, user.getId());
        for (Map.Entry<Integer, BigDecimal> recipient : balances.entrySet()) {
            if (recipient.getKey().equals(user.getId())) continue;
            if (recipient.getValue().compareTo(BigDecimal.ZERO) > 0) {
                User recipientUser = userDAO.findUserById(recipient.getKey());
                Email email = new Email(recipientUser.getEmail(), "Fridget Debt Information",
                    "<h1>Debt overview</h1>"
                        + "You owe " + user.getName() + " "
                        + recipient.getValue() + currencySymbol
                        + "<br /><br />"
                        + "<b>Please take care of the debt coverage in the near future.</b>");
                emailService.send(email);
            }
        }
    }

}
