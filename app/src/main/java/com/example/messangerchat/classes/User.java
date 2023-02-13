package com.example.messangerchat.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class User implements Serializable {

    private String uid;
    private String username;
    private String password;
    private String email;
    private String profilePhotoUrl;

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String uid, String username, String password, String email, String profilePhotoUrl) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public static boolean isUserExist(String username, String password, ArrayList<User> users) {
        int countUsers = users.size();
        for (int i = 0; i < countUsers; i++) {
            if (users.get(i).getUsername().equals(username) ||
                    users.get(i).getPassword().equals(password))
                return true;
        }
        return false;
    }

    public static boolean isUserEmailExist(String email, ArrayList<User> users) {
        int countUsers = users.size();
        for (int i = 0; i < countUsers; i++) {
            if (users.get(i).getEmail().equals(email))
                return true;
        }
        return false;
    }

    public static int registerUser(User user, String confirmPassword, ArrayList<User> users) {
        if (user.getPassword().length() < 5 || !user.getPassword().equals(confirmPassword))
            return 1; //"The password is not correct!"
        if (User.isUserExist(user.getUsername(), user.getPassword(), users))
            return 2; //"Username or password already exists!"
        if (User.isUserEmailExist(user.getEmail(), users))
            return 3; //"User with this email already exists!"
        if (!isValidEmail(user.getEmail()))
            return 4; //"Email is not valid!"
        users.add(user);
        return 5; //"User added"
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile("^.+@.+\\..+$")
                .matcher(email)
                .matches();
    }
}
