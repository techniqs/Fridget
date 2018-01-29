package at.ac.tuwien.sepm.fridget.common.entities;

import java.time.LocalDateTime;

public class PasswordResetCode {

    private int id = -1;
    private User user = null;
    private String code = null;
    private LocalDateTime createdAt = null;


    public PasswordResetCode() {
    }

    public PasswordResetCode(User user, String code) {
        this.user = user;
        this.code = code;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
