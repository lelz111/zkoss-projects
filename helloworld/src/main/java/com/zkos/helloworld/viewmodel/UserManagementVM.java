package com.zkos.helloworld.viewmodel;

import com.zkos.helloworld.model.Role;
import com.zkos.helloworld.model.User;
import com.zkos.helloworld.service.RoleService;
import com.zkos.helloworld.service.UserService;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zkplus.spring.SpringUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class UserManagementVM {
    private final UserService userService = (UserService) SpringUtil.getBean("userService");
    private final RoleService roleService = (RoleService) SpringUtil.getBean("roleService");
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private List<User> users = userService.getAllUsers();
    private List<User> filteredUsers = new ArrayList<>(users);

    private String npk;
    private String nama;
    private String posisi;
    private String status;
    private Image imageMedia;
    private String fileLabel;
    private String password;

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
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    public boolean isAdmin() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public AImage userImageMedia(User user) {
        if (user.getImageData() != null) {
            try {
                return new AImage(user.getNpk() + ".png", user.getImageData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Command
    @NotifyChange({"filteredUsers", "npk", "nama", "posisi", "status", "imageMedia", "fileLabel", "editMode", "password"})
    public void save() {
        if (!isAdmin()) {
            org.zkoss.zk.ui.util.Clients.showNotification("Access denied.", "error", null, "top_center", 2000);
            return;
        }
        if (npk == null || npk.trim().isEmpty() ||
                nama == null || nama.trim().isEmpty() ||
                posisi == null || posisi.trim().isEmpty() ||
                status == null || status.trim().isEmpty() ||
                (!editMode && (password == null || password.trim().isEmpty()))) {
            org.zkoss.zk.ui.util.Clients.showNotification("All fields are required!", "warning", null, "top_center", 2000);
            return;
        }

        byte[] imgBytes = null;
        if (imageMedia != null) {
            try (InputStream is = imageMedia.getStreamData();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] data = new byte[16384];
                int nRead;
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                imgBytes = buffer.toByteArray();
            } catch (IOException e) {
                org.zkoss.zk.ui.util.Clients.showNotification("Failed to read image!", "error", null, "top_center", 2000);
                return;
            }
        }

        User user;
        if (editMode && editId != null) {
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
            user = new User();
            user.setNpk(npk);
            user.setNamaKaryawan(nama);
            user.setPosisi(posisi);
            user.setStatus(status);
            user.setImageData(imgBytes);
            user.setPassword(passwordEncoder.encode(password));
            userService.addUser(user);

            Role defaultRole = roleService.findByName("USER");
            if (defaultRole != null) {
                user.getRoles().add(defaultRole);
                userService.updateUser(user);
            }
        }

        users = userService.getAllUsers();
        filteredUsers = new ArrayList<>(users);
        resetForm();
    }

    @Command
    @NotifyChange("*")
    public void edit(@BindingParam("user") User user, @BindingParam("index") int index) {
        if (!isAdmin()) {
            org.zkoss.zk.ui.util.Clients.showNotification("Access denied.", "error", null, "top_center", 2000);
            return;
        }
        this.editMode = true;
        this.editId = user.getId();
        this.npk = user.getNpk();
        this.nama = user.getNamaKaryawan();
        this.posisi = user.getPosisi();
        this.status = user.getStatus();
        this.imageMedia = userImageMedia(user);
        this.fileLabel = user.getNpk() + ".png";
        this.password = "";
    }

    @Command
    @NotifyChange("*")
    public void delete(@BindingParam("index") int index) {
        if (!isAdmin()) {
            org.zkoss.zk.ui.util.Clients.showNotification("Access denied.", "error", null, "top_center", 2000);
            return;
        }
        if (index >= 0 && index < filteredUsers.size()) {
            User userToDelete = filteredUsers.get(index);
            userService.softDeleteUser(userToDelete.getId());
            users = userService.getAllUsers();
            filteredUsers = new ArrayList<>(users);
        }
    }

    @Command
    public void view(@BindingParam("user") User user) {
        if (user == null) {
            org.zkoss.zk.ui.util.Clients.showNotification("User not found.", "error", null, "top_center", 2000);
            return;
        }
        Executions.getCurrent().setAttribute("selectedUser", user);
        Executions.createComponents("/user_detail.zul", null, null);
    }

    @Command
    @NotifyChange("*")
    public void reset() {
        resetForm();
    }

    private void resetForm() {
        this.npk = "";
        this.nama = "";
        this.posisi = "";
        this.status = "";
        this.password = "";
        this.imageMedia = null;
        this.fileLabel = "";
        this.editMode = false;
        this.editId = null;
        this.filteredUsers = new ArrayList<>(users);
    }

    @Command
    @NotifyChange("filteredUsers")
    public void search() {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            filteredUsers = new ArrayList<>(users);
        } else {
            String keywordLower = searchKeyword.toLowerCase();
            filteredUsers = users.stream().filter(u ->
                    u.getNpk().toLowerCase().contains(keywordLower) ||
                            u.getNamaKaryawan().toLowerCase().contains(keywordLower) ||
                            u.getPosisi().toLowerCase().contains(keywordLower) ||
                            u.getStatus().toLowerCase().contains(keywordLower)
            ).collect(java.util.stream.Collectors.toList());
        }
    }

    @Command
    @NotifyChange("*")
    public void uploadImage(@BindingParam("media") Media media) {
        if (media instanceof org.zkoss.image.Image) {
            this.imageMedia = (Image) media;
            this.fileLabel = media.getName();
        } else {
            org.zkoss.zk.ui.util.Clients.showNotification("Please upload an image file.", "warning", null, "top_center", 2000);
        }
    }
}