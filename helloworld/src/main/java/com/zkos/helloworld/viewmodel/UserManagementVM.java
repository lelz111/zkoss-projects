package com.zkos.helloworld.viewmodel;

import com.zkos.helloworld.model.User;
import com.zkos.helloworld.service.UserService;
import com.zkos.helloworld.service.UserServiceImpl;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserManagementVM {
    private final UserService userService = new UserServiceImpl();
    private List<User> users = userService.getAllUsers();
    private List<User> filteredUsers = new ArrayList<>(users);

    private String npk;
    private String nama;
    private String posisi;
    private String status;
    private Image imageMedia;
    private String fileLabel;

    private boolean editMode = false;
    private int editIndex = -1;

    private String searchKeyword;

    @Init
    public void init() {}

    // Bindable getters and setters
    public List<User> getFilteredUsers() { return filteredUsers; }
    public String getNpk() { return npk; }
    public void setNpk(String npk) { this.npk = npk; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getPosisi() { return posisi; }
    public void setPosisi(String posisi) { this.posisi = posisi; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Image getImageMedia() { return imageMedia; }
    public void setImageMedia(Image imageMedia) { this.imageMedia = imageMedia; }
    public String getFileLabel() { return fileLabel; }
    public void setFileLabel(String fileLabel) { this.fileLabel = fileLabel; }
    public boolean isEditMode() { return editMode; }
    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    @Command
    @NotifyChange({"filteredUsers", "npk", "nama", "posisi", "status", "imageMedia", "fileLabel", "editMode"})
    public void save() {
        User user = new User(npk, nama, posisi, status, imageMedia);
        if (editMode) {
            userService.updateUser(editIndex, user);
            users.set(editIndex, user);
            editMode = false;
        } else {
            userService.addUser(user);
        }
        resetForm();
    }

    @Command
    @NotifyChange({"npk", "nama", "posisi", "status", "imageMedia", "fileLabel", "editMode"})
    public void cancel() {
        resetForm();
    }

    private void resetForm() {
        this.npk = "";
        this.nama = "";
        this.posisi = "";
        this.status = "";
        this.imageMedia = null;
        this.fileLabel = "";
        this.editMode = false;
        this.editIndex = -1;
        this.filteredUsers = new ArrayList<>(users);
    }

    @Command
    @NotifyChange("filteredUsers")
    public void search() {
        String keyword = searchKeyword == null ? "" : searchKeyword.toLowerCase();
        filteredUsers = users.stream().filter(u ->
                u.getNpk().toLowerCase().contains(keyword) ||
                        u.getNamaKaryawan().toLowerCase().contains(keyword) ||
                        u.getPosisi().toLowerCase().contains(keyword) ||
                        u.getStatus().toLowerCase().contains(keyword)).collect(Collectors.toList());
    }

    @Command
    @NotifyChange({"filteredUsers", "searchKeyword"})
    public void reset() {
        searchKeyword = "";
        filteredUsers = new ArrayList<>(users);
    }

    @Command
    @NotifyChange({"npk", "nama", "posisi", "status", "imageMedia", "fileLabel", "editMode"})
    public void edit(@BindingParam("user") User user, @BindingParam("index") int index) {
        this.npk = user.getNpk();
        this.nama = user.getNamaKaryawan();
        this.posisi = user.getPosisi();
        this.status = user.getStatus();
        this.imageMedia = user.getImageMedia();
        this.fileLabel = "";
        this.editMode = true;
        this.editIndex = index;
    }

    @Command
    @NotifyChange("filteredUsers")
    public void delete(@BindingParam("index") int index) {
        userService.deleteUser(index);
        users.remove(index);
        filteredUsers.remove(index);
    }

    @Command
    public void view(@BindingParam("user") User user) {
        Executions.createComponents("/user_detail.zul", null, java.util.Collections.singletonMap("user", user));
    }

    @Command
    @NotifyChange({"imageMedia", "fileLabel"})
    public void uploadImage(@BindingParam("media") Media media) throws IOException {
        AImage aimg = new AImage(media.getName(), media.getStreamData());
        this.imageMedia = aimg;
        this.fileLabel = media.getName();
    }
}