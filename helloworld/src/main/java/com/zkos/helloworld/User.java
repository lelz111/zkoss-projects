package com.zkos.helloworld;

import org.zkoss.image.Image;

public class User {
    private String username;
    private String password;
    private Image imageMedia;

    public User() {}

    public User(String username, String password, Image imageMedia) {
        this.username = username;
        this.password = password;
        this.imageMedia = imageMedia;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Image getImageMedia() {
        return imageMedia;
    }

    public void setImageMedia(Image imageMedia) {
        this.imageMedia = imageMedia;
    }
}