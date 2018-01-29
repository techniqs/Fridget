package at.ac.tuwien.sepm.fridget.server.persistence.h2;

import at.ac.tuwien.sepm.fridget.common.entities.Event;
import at.ac.tuwien.sepm.fridget.common.entities.EventType;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository("eventDAO")
public class EventDAOH2 implements EventDAO {

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

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BillDAO billDAO;

    @Autowired
    private GroupDAO groupDAO;

    /**
     * Table name
     */
    private static final String TABLE_NAME = "Event";

    /**
     * Fields of table
     */
    private static final String[] FIELDS = new String[] {"type", "undone", "occured_at", "description",
        "user_id", "user_caused_id", "bill_before_id", "bill_after_id", "group_id"};

    public EventDAOH2() {
    }

    @Override
    public Event createEvent(Event event) throws PersistenceException, InvalidArgumentException {
        if (event == null) {
            throw new InvalidArgumentException("Event must not be null!");
        }
        try {
            String sql = h2Util.generateInsertString(TABLE_NAME, FIELDS);

            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, event.getType().getId());
            statement.setBoolean(2, event.isUndone());
            statement.setTimestamp(3, Timestamp.valueOf(event.getOccuredAt()));
            statement.setString(4, event.getDescription());
            if (event.getType().equals(EventType.INVITATION_ACCEPTED) && event.getSource() == null)
                statement.setNull(5, Types.INTEGER);
            else if(!event.getType().equals(EventType.INVITATION_ACCEPTED) && event.getSource() == null)
                throw new PersistenceException("Event source must not be null!");
            else
                statement.setInt(5, event.getSource().getId());
            if (event.getTarget() == null)
                statement.setNull(6, Types.INTEGER);
            else
                statement.setInt(6, event.getTarget().getId());
            if (event.getBillBefore() == null)
                statement.setNull(7, Types.INTEGER);// SQL type 4 corresponds to integer
            else
                statement.setInt(7, event.getBillBefore().getId());
            if (event.getBillAfter() == null)
                statement.setNull(8, Types.INTEGER);
            else
                statement.setInt(8, event.getBillAfter() == null ? -1 : event.getBillAfter().getId());
            statement.setInt(9, event.getGroup().getId());

            // Execute and retrieve the generated id
            statement.executeUpdate();

            // Update DTO
            event.setId(h2Util.getIdOfInserted(statement.getGeneratedKeys()));
            return event;
        } catch (NullPointerException | SQLException e) {
            throw new PersistenceException("SQL Exception thrown in Create Event DAO.", e);
        }
    }

    @Override
    public List<Event> findAllEventsForGroup(int groupId) throws PersistenceException, InvalidArgumentException {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM " + TABLE_NAME + " WHERE group_id = ?");
            stmt.setInt(1, groupId);

            List<Event> events = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    Event event = new Event();
                    event.setId(rs.getInt("id"));
                    int i = 0;
                    event.setType(EventType.fromId(rs.getInt(FIELDS[i++])));
                    event.setUndone(rs.getBoolean(FIELDS[i++]));
                    event.setOccuredAt(rs.getTimestamp(FIELDS[i++]).toLocalDateTime());
                    event.setDescription(rs.getString(FIELDS[i++]));
                    event.setSource(userDAO.findUserById(rs.getInt(FIELDS[i++])));
                    event.setTarget(userDAO.findUserById(rs.getInt(FIELDS[i++])));
                    if (rs.getInt(FIELDS[i++]) < 1)
                        event.setBillBefore(null);
                    else
                        event.setBillBefore(billDAO.findBillById(rs.getInt("bill_before_id")));
                    if (rs.getInt(FIELDS[i++]) < 1)
                        event.setBillAfter(null);
                    else
                        event.setBillAfter(billDAO.findBillById(rs.getInt("bill_after_id")));
                    event.setGroup(groupDAO.getGroup(groupId));
                    events.add(event);
                }
            } catch (PersistenceException | InvalidArgumentException e) {
                throw e;
            }
            return events;
        } catch (SQLException e) {
            throw new PersistenceException("Persistence exception thrown while retrieving events for group.", e);
        }
    }

    @Override
    public List<Event> findEventsForEventLog(User user, int limit) throws PersistenceException, InvalidArgumentException {
        try {
            // Step 1 - get all groups the user is in
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT group_id FROM GROUPINVITATION WHERE accepted = TRUE AND user_id = ?"
            );
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            List<Integer> groupIds = new ArrayList<>();
            try {
                while (rs.next()) {
                    groupIds.add(rs.getInt(1));
                }
                // Return empty list if user is not in any groups
                if (groupIds.size() == 0) {
                    return new ArrayList<>();
                }
                String groupIdsStr = groupIds.toString();
                groupIdsStr = groupIdsStr.substring(1, groupIdsStr.length() - 1);// Now the format is like "1, 2, 3"
                // Generate right number of placeholder question marks
                StringBuilder questionMarks = new StringBuilder();
                for (int i = 0; i < groupIds.size(); i++)
                    questionMarks.append("?,");
                questionMarks.deleteCharAt(questionMarks.lastIndexOf(","));
                // Get the newest 10 event over all groups the user is in
                stmt = connection.prepareStatement(
                    "SELECT * FROM " + TABLE_NAME + " WHERE group_id IN (" + questionMarks.toString()
                        + ") ORDER BY occured_at DESC LIMIT ?");
                for (int i = 1; i < groupIds.size() + 1; i++)
                    stmt.setInt(i, groupIds.get(i - 1));
                stmt.setInt(groupIds.size() + 1, limit);

                List<Event> events = new ArrayList<>();
                rs = stmt.executeQuery();
                while (rs.next()) {
                    Event event = new Event();
                    event.setId(rs.getInt("id"));
                    int i = 0;
                    event.setType(EventType.fromId(rs.getInt(FIELDS[i++])));
                    event.setUndone(rs.getBoolean(FIELDS[i++]));
                    event.setOccuredAt(rs.getTimestamp(FIELDS[i++]).toLocalDateTime());
                    event.setDescription(rs.getString(FIELDS[i++]));
                    event.setSource(userDAO.findUserById(rs.getInt(FIELDS[i++])));
                    event.setTarget(userDAO.findUserById(rs.getInt(FIELDS[i++])));
                    if (rs.getInt(FIELDS[i++]) < 1)
                        event.setBillBefore(null);
                    else
                        event.setBillBefore(billDAO.findBillById(rs.getInt("bill_before_id")));
                    if (rs.getInt(FIELDS[i++]) < 1)
                        event.setBillAfter(null);
                    else
                        event.setBillAfter(billDAO.findBillById(rs.getInt("bill_after_id")));
                    event.setGroup(groupDAO.getGroup(rs.getInt("group_id")));
                    events.add(event);
                }
                return events;
            } catch (PersistenceException | InvalidArgumentException e) {
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Persistence exception thrown while retrieving events for group.", e);
        }
    }

}
