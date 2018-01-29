package at.ac.tuwien.sepm.fridget.common.util;

public class ResetPasswordArguments extends VerifyResetCodeArguments {

    private String password;

    public ResetPasswordArguments() {
        super();
    }

    public ResetPasswordArguments(String email, String code, String password) {
        super(email, code);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
