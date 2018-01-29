package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupDAOH2Tests extends TestBase {

    @Autowired
    GroupDAO groupDAO;


    @Autowired
    TestUtil testUtil;

    @Override
    public void setUp() throws Exception {

    }

    @Override
    public void tearDown() {

    }

    @Test
    public void createValidGroup_ShouldReturnGroupWithID() throws InvalidArgumentException, PersistenceException {

        Group group = testUtil.createValidGroup();
        group = groupDAO.createGroup(group);

        assertThat(group.getId()).isNotNull();
        assertThat(group.getId()).isGreaterThan(-1);
        assertThat(group.getName()).isEqualTo("Testgroup");
        assertThat(group.getName()).isNotNull();
        assertThat(group.getMembers()).isNotNull();

    }

    @Test(expected = InvalidArgumentException.class)
    public void createInvalidGroupWithNull_ShouldThrowInvalidArgumentException() throws InvalidArgumentException, PersistenceException {
        groupDAO.createGroup(null);
    }

    @Test
    public void editGroupWithInvalidGroupId_ShouldNotFail() throws PersistenceException {
        groupDAO.editGroup(-1, "NewName");
    }

    @Test
    public void editGroupWithValidArguments_ShouldNotFail() throws PersistenceException, InvalidArgumentException {
        groupDAO.editGroup(testUtil.createValidGroup().getId(), "NewName");
    }

    @Test
    public void rejectAllPendingInvitationsWithInvalidGroupId_ShouldNotFail() throws PersistenceException {
        groupDAO.rejectAllPendingInvitations(-1);
    }

    @Test
    public void rejectAllPendingInvitationsWithValidGroupId_ShouldNotFail() throws PersistenceException, InvalidArgumentException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupDAO.getGroupsWithMembersForUser(1).contains(group)).isTrue();
        assertThat(groupDAO.getUserIdsOfGroup(group.getId()).isEmpty()).isFalse();
        groupDAO.leaveGroup(group.getId(), 1);
        assertThat(groupDAO.getUserIdsOfGroup(group.getId()).isEmpty()).isTrue();
        groupDAO.rejectAllPendingInvitations(group.getId());
        assertThat(groupDAO.getGroupsWithMembersForUser(1).contains(group)).isFalse();
    }

    @Test
    public void rejectAllPendingInvitationsWithMembersStillInIt_ShouldNotFail() throws PersistenceException, InvalidArgumentException {
        Group group = testUtil.createDbValidGroup();
        assertThat(groupDAO.getGroupsWithMembersForUser(1).contains(group)).isTrue();
        assertThat(groupDAO.getUserIdsOfGroup(group.getId()).isEmpty()).isFalse();
        groupDAO.inviteUserToGroup(group.getId(), 5);
        groupDAO.leaveGroup(group.getId(), 1);
        groupDAO.rejectAllPendingInvitations(group.getId());
        assertThat(groupDAO.getPendingInvitationsForSpecificGroup(group.getId(), -1).isEmpty()).isTrue();
        assertThat(groupDAO.getGroupsWithMembersForUser(1).contains(group)).isFalse();
    }

    @Test
    public void leaveGroupWithValidArguments_ShouldNotFail() throws PersistenceException, InvalidArgumentException {
        Group group = testUtil.createDbValidGroup();
        groupDAO.acceptInvitation(groupDAO.inviteUserToGroup(group.getId(),2));
        group.addMembers(2);
        assertThat(groupDAO.getGroupsWithMembersForUser(1).contains(group)).isTrue();
        assertThat(groupDAO.getUserIdsOfGroup(group.getId()).isEmpty()).isFalse();
        groupDAO.leaveGroup(group.getId(),1);
        assertThat(groupDAO.getUserIdsOfGroup(group.getId()).contains(1)).isFalse();
        groupDAO.leaveGroup(group.getId(),2);
    }

    @Test
    public void leaveGroupWithInValidUserId_ShouldNotFail() throws PersistenceException, InvalidArgumentException {
        Group group = testUtil.createDbValidGroup();
        groupDAO.leaveGroup(group.getId(),-1);
    }


}
