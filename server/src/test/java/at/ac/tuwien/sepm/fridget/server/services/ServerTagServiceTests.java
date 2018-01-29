package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.TagService;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Component
public class ServerTagServiceTests extends TestBase {
    @Autowired
    TagService tagService;

    @Autowired
    TestUtil testUtil;

    @Test
    public void createTagWithValidTag_shouldWork() throws PersistenceException, InvalidArgumentException {
        User user = new User(2, "Bob", "qsefridget+bob@gmail.com");
        Tag tag = testUtil.createValidTag();
        tag.setGroupId(1);

        Tag insertedTag = tagService.createTag(user, tag);
        tag.setId(insertedTag.getId());

        assertThat(tag.getId(), is(greaterThanOrEqualTo(0)));
        assertThat(insertedTag, is(equalTo(tag)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void createTagWithInvalidName_shouldThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        User user = new User(1, "Anna", "qsefridget+anna@gmail.com");
        Tag tag = testUtil.createValidTag();
        tag.setName("");
        tag.setGroupId(1);

        tagService.createTag(user, tag);
    }

    @Test(expected = InvalidArgumentException.class)
    public void createTagWithInvalidUser_shouldThrowInvalidArgumentException() throws PersistenceException, InvalidArgumentException {
        User user = new User(9, "Ida", "qsefridget+ida@gmail.com");
        Tag tag = testUtil.createValidTag();
        tag.setGroupId(1);

        tagService.createTag(user, tag);
    }
}
