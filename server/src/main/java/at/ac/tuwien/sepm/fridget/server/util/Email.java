package at.ac.tuwien.sepm.fridget.server.util;

public class Email {
    private String[] to;
    private String subject;
    private String text;
    private String replyTo = "qsefridget+no-reply@gmail.com";
    private String from = "qsefridget+no-reply@gmail.com";

    public Email(String to, String subject, String text) {
        this.to = new String[] {to};
        this.subject = subject;
        this.text = text;
    }

    public Email(String[] to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
