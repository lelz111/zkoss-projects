package com.zkos.helloworld;

import com.zkos.helloworld.model.User;
import org.zkoss.bind.BindUtils;
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
    private String uploadedFileName = "";

    public List<User> getUserList() {
        return userList;
    }

    public User getNewUser() {
        return newUser;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    @Command
    @NotifyChange({"userList", "newUser", "editMode", "uploadedFileName"})
    public void saveUser() {
        if (editMode) {
            userList.set(editIndex, new User(
                    newUser.getNpk(),
                    newUser.getNamaKaryawan(),
                    newUser.getPosisi(),
                    newUser.getStatus(),
                    newUser.getImageMedia()
            ));
        } else {
            userList.add(new User(
                    newUser.getNpk(),
                    newUser.getNamaKaryawan(),
                    newUser.getPosisi(),
                    newUser.getStatus(),
                    newUser.getImageMedia()
            ));
        }
        newUser = new User();
        editMode = false;
        uploadedFileName = "";
    }

    @Command
    @NotifyChange({"newUser", "uploadedFileName"})
    public void uploadImage(@BindingParam("media") Media media) {
        if (media instanceof Image) {
            newUser.setImageMedia((Image) media);
            uploadedFileName = media.getName();
        } else {
            Messagebox.show("Please upload an image file.");
        }
    }

    @Command
    @NotifyChange({"newUser", "editMode", "uploadedFileName"})
    public void editUser(@BindingParam("user") User user, @BindingParam("index") int index) {
        newUser = new User(
                user.getNpk(),
                user.getNamaKaryawan(),
                user.getPosisi(),
                user.getStatus(),
                user.getImageMedia()
        );
        editIndex = index;
        editMode = true;
        uploadedFileName = "";
    }

    @Command
    @NotifyChange("userList")
    public void deleteUser(@BindingParam("index") int index) {
        Messagebox.show("Apakah Anda yakin ingin menghapus data ini?",
                "Konfirmasi", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                event -> {
                    if (Messagebox.ON_YES.equals(event.getName())) {
                        userList.remove(index);
                        BindUtils.postNotifyChange(null, null, this, "userList");
                    }
                });
    }

    @Command
    @NotifyChange({"newUser", "editMode", "uploadedFileName"})
    public void cancelEdit() {
        newUser = new User();
        editMode = false;
        uploadedFileName = "";
    }
}
