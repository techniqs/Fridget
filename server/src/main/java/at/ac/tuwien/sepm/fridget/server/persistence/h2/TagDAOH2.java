package at.ac.tuwien.sepm.fridget.server.persistence.h2;

import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.persistence.H2Util;
import at.ac.tuwien.sepm.fridget.server.persistence.TagDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository("tagDAO")
public class TagDAOH2 implements TagDAO {

    private static final String CREATE_TAG_QUERY = "INSERT INTO Tag (name, group_id) VALUES (?, ?)";
    private static final String GET_TAGS_QUERY = "SELECT * FROM Tag WHERE group_id = ? OR group_id = -1";

    @Autowired
    Connection connection;

    @Autowired
    H2Util h2Util;

    public TagDAOH2(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Tag createTag(Tag tag) throws PersistenceException {
        try (
            PreparedStatement statement = connection.prepareStatement(CREATE_TAG_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, tag.getName());
            statement.setInt(2, tag.getGroupId());

            statement.executeUpdate();

            tag.setId(h2Util.getIdOfInserted(statement.getGeneratedKeys()));

            return tag;
        } catch (SQLException e) {
            throw new PersistenceException("Error while creating Tag", e);
        }
    }

    @Override
    public List<Tag> getTagsForGroup(int groupId) throws PersistenceException {
        try (
            PreparedStatement statement = connection.prepareStatement(GET_TAGS_QUERY);
        ) {
            statement.setInt(1, groupId);
            ResultSet resultSet = statement.executeQuery();

            List<Tag> tags = new ArrayList<>();
            while (resultSet.next()) {
                Tag tag = new Tag();
                tag.setId(resultSet.getInt("id"));
                tag.setName(resultSet.getString("name"));
                tag.setGroupId(resultSet.getInt("group_id"));
                tags.add(tag);
            }
            return tags;
        } catch (SQLException e) {
            throw new PersistenceException("Error while querying Tags", e);
        }
    }
}
