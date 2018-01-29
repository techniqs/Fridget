package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.RestClient;
import at.ac.tuwien.sepm.fridget.common.entities.Tag;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import at.ac.tuwien.sepm.fridget.common.entities.UserCredentials;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;
import java.util.List;

@Service
public class ClientTagService implements TagService {

    @Autowired
    RestClient restClient;

    @Override
    public Tag createTag(User user, Tag tag) throws PersistenceException, InvalidArgumentException {
        try {
            return restClient.postForObject(
                "/group/{groupId}/tag/create",
                tag,
                Tag.class,
                tag.getGroupId()
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Override
    public List<Tag> getTagsForGroup(User user, int groupId) throws PersistenceException, InvalidArgumentException {
        try {
            return Arrays.asList(restClient.getForObject(
                "/group/{groupId}/tag/get",
                Tag[].class,
                groupId
            ));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidArgumentException(e.getResponseBodyAsString(), e);
            } else {
                throw new PersistenceException(e.getResponseBodyAsString(), e);
            }
        }
    }
}
