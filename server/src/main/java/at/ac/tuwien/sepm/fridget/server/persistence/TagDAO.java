package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface TagDAO {

    /**
     * Create Tag for a group
     * @param tag Tag to created for group
     * @return Returns inserted Tag
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    Tag createTag(Tag tag) throws PersistenceException;

    /**
     * Get default and custom tags for a group
     * @param groupId Id of the concerned group
     * @return A list of tags for a group
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     */
    List<Tag> getTagsForGroup(int groupId) throws PersistenceException;

    // Tag findTagById(int id);

    // List<Tag> findTagsByGroup(Group group);

}
