package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.exception.BalanceNotZeroException;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerGroupServiceTests extends TestBase {

    @Autowired
    TestUtil testUtil;

    @Autowired
    ServerGroupService groupService;

    @Test
    public void createValidGroupWithValidArguments_ShouldReturnGroupWithID() throws InvalidArgumentException, PersistenceException {
        Group group = testUtil.createValidGroup();
        group = groupService.createGroup(group, 1);

        assertThat(group.getId()).isNotNull();
        assertThat(group.getId()).isGreaterThan(-1);
        assertThat(group.getName()).isEqualTo("Testgroup");
        assertThat(group.getMembers()).isNotNull();
        assertThat(group.getName()).isNotNull();
    }

    @Test(expected = InvalidArgumentException.class)
    public void createInvalidGroupWithInvalidName_ShouldThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Group group = new Group();
        group.setName(null);
        groupService.createGroup(group, 1);
    }

    @Test(expected = PersistenceException.class)
    public void createInvalidGroupWithInvalidUserId_ShouldThrowPersistenceException() throws InvalidArgumentException, PersistenceException {
        Group group = new Group();
        group.setName("TestGroup");
        groupService.createGroup(group, -1);
    }

    @Test
    public void getGroupsWithMembersForUserWithValidUserId_ShouldNotFail() throws PersistenceException, InvalidArgumentException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupService.getGroupsWithMembersForUser(1).contains(group)).isTrue();
    }

    @Test
    public void getGroupsWithMembersForUserWithInvalidUserId_ShouldNotFail() throws PersistenceException, InvalidArgumentException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupService.getGroupsWithMembersForUser(-1).contains(group)).isFalse();
    }

    @Test(expected = InvalidArgumentException.class)
    public void editGroupWithInValidUserId_ShouldThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupService.getGroupsWithMembersForUser(1).contains(group)).isTrue();
        group.setName("NewName");
        groupService.editGroup(group, -1);
        //assertThat(groupService.getGroups(1).contains(group));
    }

    @Test(expected = InvalidArgumentException.class)
    public void editGroupWithNullName_ShouldThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupService.getGroupsWithMembersForUser(1).contains(group)).isTrue();
        group.setName(null);
        groupService.editGroup(group, 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void editGroupWithInvalidName_ShouldThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupService.getGroupsWithMembersForUser(1).contains(group)).isTrue();
        group.setName("");
        groupService.editGroup(group, 1);
    }

    @Test
    public void editGroupWithValidArguments_ShouldNotFail() throws InvalidArgumentException, PersistenceException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupService.getGroupsWithMembersForUser(1).contains(group)).isTrue();
        group.setName("NewGroupName");
        groupService.editGroup(group, 1);
        assertThat(groupService.getGroupsWithMembersForUser(1).contains(group)).isTrue();
    }

    @Test
    public void leaveGroupWithOnlyOneUserShouldDeleteGroup_ShouldNotFail() throws PersistenceException, InvalidArgumentException, BalanceNotZeroException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupService.getGroupsWithMembersForUser(1).contains(group)).isTrue();
        groupService.leaveGroup(group.getId(), 1);
        assertThat(groupService.getGroupsWithMembersForUser(1).contains(group)).isFalse();
    }

    @Test(expected = BalanceNotZeroException.class)
    public void leaveGroupWithUserStillHavingDebts_ShouldFail() throws PersistenceException, InvalidArgumentException, BalanceNotZeroException {
        groupService.leaveGroup(1, 6);
    }

    @Test(expected = BalanceNotZeroException.class)
    public void leaveGroupAsCreditor_ShouldFail() throws PersistenceException, InvalidArgumentException, BalanceNotZeroException {
        groupService.leaveGroup(1, 5);
    }

    @Test
    public void hasMemberWithMember_shouldReturnTrue() throws PersistenceException {
        assertThat(groupService.hasMember(1, 2)).isTrue();
    }

    @Test
    public void hasMemberWithoutMember_shouldReturnFalse() throws PersistenceException {
        assertThat(groupService.hasMember(1, 3)).isFalse();
    }

    @Test
    public void inviteUserToGroupWithValidArguments_shouldNotFail() throws InvalidArgumentException, PersistenceException {
        assertThat(groupService.getPendingInvitations(7)).isEmpty();
        groupService.inviteUserToGroup(1, 2, "qsefridget+grete@gmail.com");
        assertThat(groupService.getPendingInvitations(7)).isNotEmpty();
    }

    @Test(expected = InvalidArgumentException.class)
    public void inviteUserToGroupTwoTimes_shouldThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        assertThat(groupService.getPendingInvitations(4)).isEmpty();
        groupService.inviteUserToGroup(1, 2, "qsefridget+dora@gmail.com");
        groupService.inviteUserToGroup(1, 2, "qsefridget+dora@gmail.com");
    }

    @Test(expected = InvalidArgumentException.class)
    public void inviteUserToGroupWithNonTeamMember_shouldThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        groupService.inviteUserToGroup(1, 3, "qsefridget+grete@gmail.com");
    }

    @Test(expected = InvalidArgumentException.class)
    public void inviteUserToGroupWithAnAlreadyTeamMember_shouldThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        groupService.inviteUserToGroup(1, 2, "qsefridget+hans@gmail.com");
    }

    @Test
    public void acceptInvitationWithValidInvitation_shouldWork() throws InvalidArgumentException, PersistenceException {
        assertThat(groupService.hasMember(1, 3)).isFalse();
        int invitationId = groupService.inviteUserToGroup(1, 2, "qsefridget+clara@gmail.com");
        groupService.acceptInvitation(invitationId, 3);
        assertThat(groupService.hasMember(1, 3)).isTrue();
    }

    @Test(expected = InvalidArgumentException.class)
    public void acceptInvitationWithInvalidUserId_shouldNotWork() throws InvalidArgumentException, PersistenceException {
        assertThat(groupService.hasMember(9, 3)).isFalse();
        int invitationId = groupService.inviteUserToGroup(9, 6, "qsefridget+clara@gmail.com");
        groupService.acceptInvitation(invitationId, 4);
    }

    @Test
    public void rejectInvitationWithValidInvitation_shouldWork() throws InvalidArgumentException, PersistenceException {
        assertThat(groupService.hasMember(1, 4)).isFalse();
        int invitationId = groupService.inviteUserToGroup(1, 2, "qsefridget+dora@gmail.com");
        groupService.rejectInvitation(invitationId, 4);
        assertThat(groupService.hasMember(1, 4)).isFalse();
    }

    @Test(expected = InvalidArgumentException.class)
    public void rejectInvitationWithInvalidUserId_shouldNotWork() throws InvalidArgumentException, PersistenceException {
        assertThat(groupService.hasMember(9, 4)).isFalse();
        int invitationId = groupService.inviteUserToGroup(9, 6, "qsefridget+dora@gmail.com");
        groupService.acceptInvitation(invitationId, 5);
    }

    @Test
    public void getGroupsForUser_shouldReturnGroups() throws PersistenceException {
        assertThat(groupService.getGroups(2)).hasSize(2);
    }
}
