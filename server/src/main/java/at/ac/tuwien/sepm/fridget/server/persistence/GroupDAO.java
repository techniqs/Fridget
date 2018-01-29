package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.entities.GroupInvitation;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface GroupDAO {


    /**
     * Insert a new group into the DB and assign the group an id
     *
     * @param group The newly created Group
     * @return the given group with an assigned id
     * @throws InvalidArgumentException Thrown on invalid arguments
     * @throws PersistenceException     Thrown on internal errors of the persistence layer
     */
    Group createGroup(Group group) throws InvalidArgumentException, PersistenceException;

    /**
     * Edit the name of a group
     *
     * @param groupId Id of the concerned group
     * @param newName New name of the group
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    void editGroup(int groupId, String newName) throws PersistenceException;

    /**
     * Invite a certain user to a group, the person inviting the other user must already be a member of the said group
     *
     * @param groupId Id of the concerned group
     * @param userId  Id of the to be invited party
     * @return Created invitation id
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    int inviteUserToGroup(int groupId, int userId) throws PersistenceException;

    /**
     * Accept the invitation for a group
     *
     * @param invitationId Id of the invitation to be accepted
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    void acceptInvitation(int invitationId) throws PersistenceException;

    /**
     * Reject the invitation for a group
     *
     * @param invitationId Id of the invitation to be rejected
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    void rejectInvitation(int invitationId) throws PersistenceException;

    /**
     * Removes a user from a group; Can only be called by the user itself
     *
     * @param groupId Id of the group
     * @param userId  Id of the user leaving the group
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    void leaveGroup(int groupId, int userId) throws PersistenceException;

    /**
     * Retrieve all user ids for a certain group
     *
     * @param groupId Id of the group
     * @return The ids of all group members
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    List<Integer> getUserIdsOfGroup(int groupId) throws PersistenceException;

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


    List<GroupInvitation> getPendingInvitationsForSpecificGroup(int groupId, int userId) throws PersistenceException;

    /**
     * Deleting specific group
     *
     * @param groupId id of the group to be deleted
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    void rejectAllPendingInvitations(int groupId) throws PersistenceException;
    /**
     * Retrieve Group by Id
     * @param groupId Id of concerned group
     * @return Group for specified groupId
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    Group getGroup(int groupId) throws PersistenceException, InvalidArgumentException;

}
