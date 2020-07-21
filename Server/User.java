package game.Server;

import java.io.Serializable;
import java.net.PasswordAuthentication;
import java.util.Arrays;

public class User implements Serializable {
    private String userName, password;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User)obj;
        return user.password.equals(password) &&
                user.userName.equals(userName);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
