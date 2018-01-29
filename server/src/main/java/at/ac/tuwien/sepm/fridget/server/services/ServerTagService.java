package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import at.ac.tuwien.sepm.fridget.common.services.TagService;
import at.ac.tuwien.sepm.fridget.server.persistence.TagDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ServerTagService implements TagService {

    @Autowired
    TagDAO tagDAO;
    @Autowired
    GroupService groupService;

    @Override
    public Tag createTag(User user, Tag tag) throws PersistenceException, InvalidArgumentException {
        // Validate Tag
        validateTag(tag);

        // Ensure user is allowed to create tag for group
        if (!groupService.hasMember(tag.getGroupId(), user.getId())) {
            throw new InvalidArgumentException("User is not member of group");
        }

        // Create tag
        return tagDAO.createTag(tag);
    }

    @Override
    public List<Tag> getTagsForGroup(User user, int groupId) throws PersistenceException, InvalidArgumentException {
        // Ensure user is allowed to query tags for group
        if (!groupService.hasMember(groupId, user.getId())) {
            throw new InvalidArgumentException("User is not member of group");
        }

        // Query tags
        return tagDAO.getTagsForGroup(groupId);
    }

    private static void validateTag(Tag tag) throws InvalidArgumentException {
        if (tag.getName() == null || tag.getName().isEmpty()) throw new InvalidArgumentException("Tag needs a name");
    }
}
