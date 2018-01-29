package at.ac.tuwien.sepm.fridget.common.util;

public class VerifyResetCodeArguments {

    private String email;
    private String code;

    public VerifyResetCodeArguments() {
    }

    public VerifyResetCodeArguments(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
