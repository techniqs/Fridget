package at.ac.tuwien.sepm.fridget.common.entities;

public class UserInvitation {

    private String inviteeEmail;
    private int groupId;

    public UserInvitation() {
    }

    public UserInvitation(String inviteeEmail, int groupId) {
        this.inviteeEmail = inviteeEmail;
        this.groupId = groupId;
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }

    public void setInviteeEmail(String inviteeEmail) {
        this.inviteeEmail = inviteeEmail;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
