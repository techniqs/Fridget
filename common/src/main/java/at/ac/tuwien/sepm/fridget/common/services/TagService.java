package at.ac.tuwien.sepm.fridget.common.services;

import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;

import java.util.List;

public interface TagService {

    /**
     * Create Tag for a group
     * @param user User that creates tag
     * @param tag Tag to created for group
     * @return Returns inserted Tag
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    Tag createTag(User user, Tag tag) throws PersistenceException, InvalidArgumentException;

    /**
     * Get default and custom tags for a group
     * @param user User that queries tag
     * @param groupId Id of the concerned group
     * @return A list of tags for a group
     * @throws PersistenceException Thrown on internal errors of the persistence layer
     * @throws InvalidArgumentException Thrown on invalid arguments
     */
    List<Tag> getTagsForGroup(User user, int groupId) throws PersistenceException, InvalidArgumentException;

    // boolean deleteTag(int tagID);

    // Bill tagBill(Tag tag, int billID);

}
