package at.ac.tuwien.sepm.fridget.common.services;

import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.entities.GroupInvitation;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.BalanceNotZeroException;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface GroupService {


    /**
     * Create a new group, invites & accepts the creator of the group
     *
     * @param group  newly created group
     * @param userId Id of the creator
     * @return the given group with an assigned id
     * @throws InvalidArgumentException Thrown on invalid arguments
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     */
    Group createGroup(Group group, int userId) throws InvalidArgumentException, PersistenceException;


    /**
     * Edit the name of a group
     *
     * @param group  edited group
     * @param userId Id of user
     * @return true or an Exception will be thrown
     * @throws InvalidArgumentException Thrown on invalid arguments
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     */
    boolean editGroup(Group group, int userId) throws InvalidArgumentException, PersistenceException;

    /**
     * Invite a certain user to a group, the person inviting the other user must already be a member of the said group
     *
     * @param groupId      Id of the concerned group
     * @param memberId     Id of the inviting party
     * @param inviteeEmail Id of the to be invited party
     * @return Created invitation id
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    int inviteUserToGroup(int groupId, int memberId, String inviteeEmail) throws PersistenceException, InvalidArgumentException;

    /**
     * Accept the invitation for a group
     *
     * @param invitationId Id of the invitation to be accepted
     * @param userId       Id of the user accepting the invitation
     * @return true or an Exception will be thrown
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    void acceptInvitation(int invitationId, int userId) throws PersistenceException, InvalidArgumentException;

    /**
     * Reject the invitation for a group
     *
     * @param invitationId Id of the invitation to be rejected
     * @param userId       Id of the user rejecting the invitation
     * @return true or an Exception will be thrown
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    void rejectInvitation(int invitationId, int userId) throws PersistenceException, InvalidArgumentException;

    /**
     * Removes a user from a group; Can only be called by the user itself
     *
     * @param groupId Id of the group
     * @param userId  Id of the user leaving the group
     * @return true or an Exception will be thrown
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    boolean leaveGroup(int groupId, int userId) throws PersistenceException, InvalidArgumentException, BalanceNotZeroException;

    /**
     * Checks whether a user is the member of a certain group
     *
     * @param groupId Id of the group
     * @param userId  Id of the possible member
     * @return true is user is member of the group, false if not
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    boolean hasMember(int groupId, int userId) throws PersistenceException;

    /**
     * Retrieves all Groups with members for this userID
     *
     * @param userId Id of the user to get groups for
     * @return The groups for this specific user with Members
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    List<Group> getGroupsWithMembersForUser(int userId) throws PersistenceException;

    /**
     * Fetch all pending invitations for a user
     *
     * @param userId Id of user to fetch invitations for
     * @return All pending invitations for the specified user
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    List<GroupInvitation> getPendingInvitations(int userId) throws PersistenceException, InvalidArgumentException;

    /**
     * Get all groups for a user
     *
     * @param userId Id of user to fetch groups for
     * @return All groups for a user
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    List<Group> getGroups(int userId) throws PersistenceException;

    /**
     * Get all users of certain group
     *
     * @param groupId Id of group
     * @param userId  Id of user
     * @return All users of the group
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    List<User> getUsersForGroup(int groupId, int userId) throws PersistenceException, InvalidArgumentException;

    /**
     * Get a group by its ID
     *
     * @param groupId the group id
     * @return the group
     */
    Group getGroup(int groupId) throws PersistenceException, InvalidArgumentException;

    /**
     * Send emails to each group member containing the debt status to the requester
     * @param groupId the id of the group
     * @param user the requester
     * @throws InvalidArgumentException
     * @throws PersistenceException
     */
    void sendDebtInfoMail(int groupId, User user) throws InvalidArgumentException, PersistenceException;
}
