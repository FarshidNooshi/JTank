package game.Server;

import java.io.Serializable;

/**
 * This class is a single user information keeper
 * to store and restore the users information.
 */
public class User implements Serializable {

    private String userName, password;

    /**
     * The main constructor of the User class.
     *
     * @param userName the username
     * @param password the password
     */
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
        User user = (User) obj;
        return user.password.equals(password) &&
                user.userName.equals(userName);
    }

    /**
     * A getter method for getting the user name.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * A getter method for getting the user password.
     *
     * @return the user password
     */
    public String getPassword() {
        return password;
    }
}
