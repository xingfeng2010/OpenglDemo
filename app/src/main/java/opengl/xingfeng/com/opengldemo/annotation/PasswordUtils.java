package opengl.xingfeng.com.opengldemo.annotation;

import java.util.List;

public class PasswordUtils {
    @UserCase(id = 47, description = "password must contain at least one numeric")
    public boolean validatePassword(String password) {
        return (password.matches("\\w*\\d\\w*"));
    }

    @UserCase(id = 48)
    public String encryPassword(String password) {
        return new StringBuilder(password).reverse().toString();
    }

    @UserCase(id = 49, description = "new password can't equal previously used ones")
    public boolean checkForNewPassword(List<String>previousPasswords, String password) {
        return !previousPasswords.contains(password);
    }
}
