package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import at.ac.tuwien.sepm.fridget.server.persistence.h2.TagDAOH2;
import at.ac.tuwien.sepm.fridget.server.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class TagDAOH2Tests extends TestBase {

    @Autowired
    TagDAOH2 tagDAO;

    @Autowired
    TestUtil testUtil;

    @Test
    public void createTagWithValidTag_shouldReturnInsertedTag() throws PersistenceException {
        Tag tag = testUtil.createValidTag();
        tag.setGroupId(1);
        Tag insertedTag = tagDAO.createTag(tag);
        tag.setId(insertedTag.getId());

        assertThat(tag.getId(), is(greaterThanOrEqualTo(0)));
        assertThat(tag, is(equalTo(insertedTag)));
    }
}
