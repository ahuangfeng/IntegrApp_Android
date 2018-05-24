package com.integrapp.integrapp.Chat;


import com.integrapp.integrapp.Model.User;

public class Chat {

    private User user;

    public Chat() {}
    public Chat(User user) {
        this.user = user;
    }

    public String getChatToId() {
        return user.getId();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToUsername() {
        return user.getUsername();
    }

    public String getToName() {
        return user.getName();
    }

    public User getUser() {
        return user;
    }
}
