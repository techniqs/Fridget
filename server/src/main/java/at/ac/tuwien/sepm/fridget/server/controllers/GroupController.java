package at.ac.tuwien.sepm.fridget.server.controllers;

import at.ac.tuwien.sepm.fridget.common.entities.*;
import at.ac.tuwien.sepm.fridget.common.exception.BalanceNotZeroException;
import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import at.ac.tuwien.sepm.fridget.common.services.GroupService;
import at.ac.tuwien.sepm.fridget.common.services.TagService;
import at.ac.tuwien.sepm.fridget.server.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    GroupService groupService;
    @Autowired
    TagService tagService;

    @RequestMapping(value = "/createGroup", method = RequestMethod.POST)
    public ResponseEntity<Group> createGroup(@RequestBody Group received, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called createGroup");
        User user = AuthUtils.extractUser(authentication);

        return new ResponseEntity<>(groupService.createGroup(received, user.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/editGroup", method = RequestMethod.POST)
    public ResponseEntity editGroup(@RequestBody Group received, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called editGroup");
        User user = AuthUtils.extractUser(authentication);

        return new ResponseEntity(groupService.editGroup(received, user.getId()), HttpStatus.OK);

    }

    @RequestMapping(value = "/getGroups", method = RequestMethod.GET)
    public ResponseEntity<List<Group>> getGroups(Authentication authentication) throws PersistenceException {
        LOG.info("called getGroups");
        User user = AuthUtils.extractUser(authentication);

        return new ResponseEntity<>(groupService.getGroups(user.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{groupId}/getUsersForGroup", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsersForGroup(@PathVariable int groupId, Authentication authentication)
        throws PersistenceException, InvalidArgumentException {
        LOG.info("called getUsersForGroup");
        User user = AuthUtils.extractUser(authentication);

        return new ResponseEntity<>(groupService.getUsersForGroup(groupId, user.getId()), HttpStatus.OK);
    }


    @RequestMapping(value = "/inviteUserToGroup", method = RequestMethod.POST)
    public ResponseEntity<Integer> inviteUserToGroup(@RequestBody UserInvitation userInvitation, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called inviteUserToGroup");
        User user = AuthUtils.extractUser(authentication);

        return new ResponseEntity<>(groupService.inviteUserToGroup(userInvitation.getGroupId(), user.getId(), userInvitation.getInviteeEmail()), HttpStatus.OK);
    }


    @RequestMapping(value = "/leaveGroup", method = RequestMethod.POST)
    public ResponseEntity<Group> leaveGroup(@RequestBody Group received, Authentication authentication) throws PersistenceException, InvalidArgumentException, BalanceNotZeroException {
        LOG.info("called leaveGroup");
        User user = AuthUtils.extractUser(authentication);

        return new ResponseEntity (groupService.leaveGroup(received.getId(), user.getId()), HttpStatus.OK);

    }

    @RequestMapping(value = "/{groupId}/tag/get", method = RequestMethod.GET)
    public List<Tag> getTagsForGroup(@PathVariable int groupId, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called getTagsForGroup");
        User user = AuthUtils.extractUser(authentication);
        return tagService.getTagsForGroup(user, groupId);
    }

    @RequestMapping(value = "/{groupId}/tag/create", method = RequestMethod.POST)
    public Tag createTag(@RequestBody Tag tag, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called /tag/create");
        User user = AuthUtils.extractUser(authentication);
        return tagService.createTag(user, tag);
    }

    @RequestMapping(value = "/getGroup")
    public Group getGroup(@RequestBody int id) throws PersistenceException, InvalidArgumentException {
        return groupService.getGroup(id);
    }

    @RequestMapping(value = "/getPendingInvitations")
    public ResponseEntity<List<GroupInvitation>> getPendingInvitations(Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called getPendingInvitations");
        User user = AuthUtils.extractUser(authentication);

        return new ResponseEntity<>(groupService.getPendingInvitations(user.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/acceptInvitation", method = RequestMethod.POST)
    public ResponseEntity<String> acceptInvitation(@RequestBody int invitationId, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called acceptInvitation");
        User user = AuthUtils.extractUser(authentication);

        groupService.acceptInvitation(invitationId, user.getId());
        int a = 2;
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/rejectInvitation", method = RequestMethod.POST)
    public ResponseEntity<String> rejectInvitation(@RequestBody int invitationId, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called recectInvitation");
        User user = AuthUtils.extractUser(authentication);

        groupService.rejectInvitation(invitationId, user.getId());
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/sendDebtInfoMail", method = RequestMethod.POST)
    public ResponseEntity<String> requestCompensation(@RequestBody int groupId, Authentication authentication) throws PersistenceException, InvalidArgumentException {
        LOG.info("called sendDebtInfoMail");
        User user = AuthUtils.extractUser(authentication);
        groupService.sendDebtInfoMail(groupId, user);
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
