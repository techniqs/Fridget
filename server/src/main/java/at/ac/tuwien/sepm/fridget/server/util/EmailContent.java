package at.ac.tuwien.sepm.fridget.server.util;

import at.ac.tuwien.sepm.fridget.common.entities.User;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailContent {

    private static final String TITLE_INVITE_MEMBER_TO_GROUP = "Fridget: User Invitation";
    private static final String CONTENT_INVITE_MEMBER_TO_GROUP = "User {{username}} has been invited to {{group}}";

    private static final String TITLE_RESET_PASSWORD = "Fridget: Reset Password";
    private static final String CONTENT_RESET_PASSWORD = "You can reset your password with this code: {{code}}";

    public static Email generateInviteMemberToGroupEmail(List<User> groupMembers, String username, String group) {
        String content = EmailTemplate.getInstance().fillTemplate(
            TITLE_INVITE_MEMBER_TO_GROUP,
            TemplateString.create(CONTENT_INVITE_MEMBER_TO_GROUP, new Pair<>("username", username), new Pair<>("group", group))
        );
        return new Email(
            groupMembers.stream().map(User::getEmail).toArray(String[]::new),
            TITLE_INVITE_MEMBER_TO_GROUP,
            content
        );
    }

    public static Email generateResetPasswordEmail(User user, String code) {
        String content = EmailTemplate.getInstance().fillTemplate(
            TITLE_RESET_PASSWORD,
            TemplateString.create(CONTENT_RESET_PASSWORD, new Pair<>("code", code))
        );
        return new Email(
            user.getEmail(),
            TITLE_RESET_PASSWORD,
            content
        );
    }

}
