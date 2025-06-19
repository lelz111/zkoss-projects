package com.zkos.helloworld.viewmodel;

import com.zkos.helloworld.model.User;
import com.zkos.helloworld.service.UserService;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zkplus.spring.SpringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserManagementVM {
    private final UserService userService = (UserService) SpringUtil.getBean("userService");
    private List<User> users = userService.getAllUsers();
    private List<User> filteredUsers = new ArrayList<>(users);

    private String npk;
    private String nama;
    private String posisi;
    private String status;
    private Image imageMedia;
    private String fileLabel;

    private boolean editMode = false;
    private Long editId = null;

    private String searchKeyword;

    @Init
    public void init() {}

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
        if (npk == null || npk.trim().isEmpty() ||
                nama == null || nama.trim().isEmpty() ||
                posisi == null || posisi.trim().isEmpty() ||
                status == null || status.trim().isEmpty()) {
            org.zkoss.zk.ui.util.Clients.showNotification("All fields are required!", "warning", null, "top_center", 2000);
            return;
        }
        byte[] imgBytes = null;
        if (imageMedia != null) {
            try (InputStream is = imageMedia.getStreamData()) {
                imgBytes = is.readAllBytes();
            } catch (IOException e) {
                org.zkoss.zk.ui.util.Clients.showNotification("Failed to read image!", "error", null, "top_center", 2000);
                return;
            }
        }
        User user;
        if (editMode && editId != null) {
            // Use existing user and update fields
            user = userService.getUserById(editId);
            if (user != null) {
                user.setNpk(npk);
                user.setNamaKaryawan(nama);
                user.setPosisi(posisi);
                user.setStatus(status);
                user.setImageData(imgBytes);
                userService.updateUser(user);
            }
            editMode = false;
            editId = null;
        } else {
            user = new User(npk, nama, posisi, status, imgBytes);
            userService.addUser(user);
        }
        users = userService.getAllUsers();
        filteredUsers = new ArrayList<>(users);
        resetForm();
    }

    public org.zkoss.image.Image userImageMedia(User user) {
        if (user.getImageData() == null) return null;
        try {
            return new org.zkoss.image.AImage("img", user.getImageData());
        } catch (java.io.IOException e) {
            return null;
        }
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
        this.editId = null;
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
        try {
            this.imageMedia = user.getImageData() != null ? new AImage("img", user.getImageData()) : null;
        } catch (IOException e) {
            this.imageMedia = null;
            org.zkoss.zk.ui.util.Clients.showNotification("Failed to load image!", "error", null, "top_center", 2000);
        }
        this.fileLabel = "";
        this.editMode = true;
        this.editId = user.getId();
    }

    @Command
    @NotifyChange("filteredUsers")
    public void delete(@BindingParam("index") int index) {
        if (index >= 0 && index < filteredUsers.size()) {
            User user = filteredUsers.get(index);
            userService.deleteUser(user.getId());
            users = userService.getAllUsers();
            filteredUsers = new ArrayList<>(users);
        }
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