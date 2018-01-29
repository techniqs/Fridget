package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.server.util.Email;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Component
public class EmailService {

    private JavaMailSender javaMailSender = new JavaMailSenderImpl();

    public void send(Email email) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(email.getTo());
            helper.setReplyTo(email.getReplyTo());
            helper.setFrom(email.getFrom());
            helper.setSubject(email.getSubject());
            helper.setText(email.getText(), true);
            javaMailSender.send(message);
        } catch (MessagingException exception) {
            System.out.println("Error on sending email to " + email.getTo());
        }
    }
}
