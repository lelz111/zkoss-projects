package com.zkos.helloworld;

import org.zkoss.bind.annotation.*;
import org.zkoss.image.Image;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Messagebox;

import java.util.ArrayList;
import java.util.List;

public class UserManagementViewModel {
    private List<User> userList = new ArrayList<>();
    private User newUser = new User();
    private boolean editMode = false;
    private int editIndex = -1;

    public List<User> getUserList() {
        return userList;
    }

    public User getNewUser() {
        return newUser;
    }

    public boolean isEditMode() {
        return editMode;
    }

    @Command
    @NotifyChange({"userList", "newUser", "editMode"})
    public void saveUser() {
        if (editMode) {
            userList.set(editIndex, new User(newUser.getUsername(), newUser.getPassword(), newUser.getImageMedia()));
        } else {
            userList.add(new User(newUser.getUsername(), newUser.getPassword(), newUser.getImageMedia()));
        }
        newUser = new User();
        editMode = false;
    }

    @Command
    @NotifyChange("newUser")
    public void uploadImage(@BindingParam("media") Media media) {
        if (media instanceof Image) {
            newUser.setImageMedia((Image) media);
        } else {
            Messagebox.show("Please upload an image file.");
        }
    }

    @Command
    @NotifyChange({"newUser", "editMode"})
    public void editUser(@BindingParam("user") User user, @BindingParam("index") int index) {
        newUser = new User(user.getUsername(), user.getPassword(), user.getImageMedia());
        editIndex = index;
        editMode = true;
    }

    @Command
    @NotifyChange("userList")
    public void deleteUser(@BindingParam("index") int index) {
        userList.remove(index);
    }

    @Command
    @NotifyChange({"newUser", "editMode"})
    public void cancelEdit() {
        newUser = new User();
        editMode = false;
    }
}