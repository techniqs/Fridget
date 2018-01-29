package at.ac.tuwien.sepm.fridget.server.persistence.h2;

import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.entities.GroupInvitation;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.persistence.GroupDAO;
import at.ac.tuwien.sepm.fridget.server.persistence.H2Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository("groupDAO")
public class GroupDAOH2 implements GroupDAO {

    /**
     * Database Util
     */
    @Autowired
    H2Util h2Util;

    /**
     * Database connection
     */
    @Autowired
    private Connection connection;

    private static final String TABLE_NAME_USERGROUP = "UserGroup";

    private static final String TABLE_NAME_GROUP_INVITATION = "GroupInvitation";

    private static final String[] FIELDS_USERGROUP = new String[] {"id", "name", "base_currency"};

    private static final String[] FIELDS_GROUP_INVITATION = new String[] {"id", "user_id", "group_id", "accepted"};

    public GroupDAOH2() {
    }


    @Override
    public Group createGroup(Group group) throws InvalidArgumentException, PersistenceException {
        if (group == null) {
            throw new InvalidArgumentException("Group must not be null");
        }
        try {
            String sql_group = h2Util.generateInsertString(TABLE_NAME_USERGROUP, Arrays.copyOfRange(FIELDS_USERGROUP, 1, FIELDS_USERGROUP.length));
            PreparedStatement statement = connection.prepareStatement(sql_group, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, group.getName());
            statement.setInt(2, group.getBaseCurrencyID());

            statement.executeUpdate();

            group.setId(h2Util.getIdOfInserted(statement.getGeneratedKeys()));

            return group;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

    @Override
    public void editGroup(int groupId, String newName) throws PersistenceException {
        String whereClausel = "id = ?";
        String sql_group = h2Util.generateUpdateString(TABLE_NAME_USERGROUP, Arrays.copyOfRange(FIELDS_USERGROUP, 1, 2), whereClausel);
        try {

            PreparedStatement statement = connection.prepareStatement(sql_group);
            statement.setString(1, newName);
            statement.setInt(2, groupId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

    @Override
    public int inviteUserToGroup(int groupId, int userId) throws PersistenceException {
        String query = h2Util.generateInsertString(
            TABLE_NAME_GROUP_INVITATION,
            Arrays.copyOfRange(FIELDS_GROUP_INVITATION, 1, FIELDS_GROUP_INVITATION.length)
        );

        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setInt(2, groupId);
            statement.setBoolean(3, false);

            statement.executeUpdate();

            return h2Util.getIdOfInserted(statement.getGeneratedKeys());
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

    @Override
    public void acceptInvitation(int invitationId) throws PersistenceException {
        String query = h2Util.generateUpdateString(
            TABLE_NAME_GROUP_INVITATION,
            new String[] {FIELDS_GROUP_INVITATION[3]},
            "id = " + invitationId
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, true);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

    @Override
    public void rejectInvitation(int invitationId) throws PersistenceException {
        String query = h2Util.generateDeleteString(
            TABLE_NAME_GROUP_INVITATION,
            "id = " + invitationId
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

    @Override
    public void leaveGroup(int groupId, int userId) throws PersistenceException {
        String query = h2Util.generateDeleteString(
            TABLE_NAME_GROUP_INVITATION,
            FIELDS_GROUP_INVITATION[1] + " = " + userId + " AND " + FIELDS_GROUP_INVITATION[2] + " = " + groupId
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }


    public List<Group> getGroupWithMembers(int groupId, int userId) throws PersistenceException {
        String query = h2Util.generateQueryString(TABLE_NAME_USERGROUP, FIELDS_USERGROUP,
            FIELDS_USERGROUP[0] + " = " + groupId);

        try (
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
        ) {
            List<Group> result = new ArrayList<>();
            while (resultSet.next()) {
                Group group = new Group(resultSet.getInt("id"), resultSet.getString("name"),resultSet.getInt("base_currency"));
                group.setMembers(getUserIdsOfGroup(groupId));
                result.add(group);
            }
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }

    }

    public List<Group> getGroupsWithMembersForUser(int userId) throws PersistenceException {

        List<Group> groups = getGroups(userId);
        for (Group group : groups) {
            group.setMembers(getUserIdsOfGroup(group.getId()));
        }
        return groups;
    }


    @Override
    public List<Integer> getUserIdsOfGroup(int groupId) throws PersistenceException {
        String query = h2Util.generateQueryString(
            TABLE_NAME_GROUP_INVITATION,
            new String[] {FIELDS_GROUP_INVITATION[1]},
            FIELDS_GROUP_INVITATION[2] + " = " + groupId + " AND " + FIELDS_GROUP_INVITATION[3] + " IS TRUE"
        );

        try (
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
        ) {
            List<Integer> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(resultSet.getInt(FIELDS_GROUP_INVITATION[1]));
            }
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

    @Override
    public List<GroupInvitation> getPendingInvitations(int userId) throws PersistenceException, InvalidArgumentException {
        String query = h2Util.generateQueryString(
            TABLE_NAME_GROUP_INVITATION,
            FIELDS_GROUP_INVITATION,
            FIELDS_GROUP_INVITATION[1] + " = " + userId + " AND " + FIELDS_GROUP_INVITATION[3] + " IS FALSE"
        );

        try (
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
        ) {
            List<GroupInvitation> result = new ArrayList<>();
            while (resultSet.next()) {
                GroupInvitation invitation = new GroupInvitation();
                invitation.setId(resultSet.getInt(FIELDS_GROUP_INVITATION[0]));
                invitation.setUser(new User(resultSet.getInt(FIELDS_GROUP_INVITATION[1])));
                invitation.setGroup(getGroup(resultSet.getInt(FIELDS_GROUP_INVITATION[2])));
                invitation.setAccepted(resultSet.getBoolean(FIELDS_GROUP_INVITATION[3]));
                result.add(invitation);
            }
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

    @Override
    public List<Group> getGroups(int userId) throws PersistenceException {
        String query = "SELECT ug.id, ug.name, ug.base_currency " +
            "FROM GroupInvitation gi JOIN UserGroup ug ON gi.group_id = ug.id " +
            "WHERE gi." + "user_id" + " = " + userId + " AND gi." + "accepted" + " IS TRUE";

        try (
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
        ) {
            List<Group> result = new ArrayList<>();
            while (resultSet.next()) {
                Group group = new Group(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
                result.add(group);
            }
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }

    public List<GroupInvitation> getPendingInvitationsForSpecificGroup(int groupId, int userId) throws PersistenceException {
        String query = h2Util.generateQueryString(
            TABLE_NAME_GROUP_INVITATION,
            FIELDS_GROUP_INVITATION,
            FIELDS_GROUP_INVITATION[2] + " = " + groupId
        );

        try (
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
        ) {
            List<GroupInvitation> result = new ArrayList<>();
            while (resultSet.next()) {
                GroupInvitation invitation = new GroupInvitation();
                invitation.setUser(new User(resultSet.getInt(FIELDS_GROUP_INVITATION[1])));
                invitation.setId(resultSet.getInt(FIELDS_GROUP_INVITATION[0]));
                invitation.setGroup(new Group(resultSet.getInt(FIELDS_GROUP_INVITATION[2])));
                invitation.setAccepted(resultSet.getBoolean(FIELDS_GROUP_INVITATION[3]));
                result.add(invitation);
            }
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("SQL Exception", e);
        }
    }


    @Override
    public void rejectAllPendingInvitations(int groupId) throws PersistenceException {

        if (getUserIdsOfGroup(groupId).size() == 0) {

            List<GroupInvitation> groupInvitations = getPendingInvitationsForSpecificGroup(groupId, -1);

            for (GroupInvitation groupInvitation : groupInvitations) {
                rejectInvitation(groupInvitation.getId());
            }
        }
    }



    @Override
    public Group getGroup(int groupId) throws PersistenceException, InvalidArgumentException {
        if (groupId < 1)
            throw new InvalidArgumentException("[GroupDAO] Invalid id for group!");

        String query = "SELECT * FROM UserGroup WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Group group = new Group();
                    group.setId(rs.getInt("id"));
                    group.setName(rs.getString("name"));
                    group.setMembers(getUserIdsOfGroup(groupId));
                    group.setBaseCurrencyID(rs.getInt("base_currency"));
                    return group;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("[GroupDAO] SQL exception while retieving group.", e);
        }
    }
}
