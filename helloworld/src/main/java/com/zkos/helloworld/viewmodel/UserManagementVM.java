package com.zkos.helloworld.viewmodel;

import com.zkos.helloworld.model.Role;
import com.zkos.helloworld.model.User;
import com.zkos.helloworld.service.RoleService;
import com.zkos.helloworld.service.UserService;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private AImage imageMedia; // ✅ Ganti dari Image ke AImage
    private String fileLabel;
    private String password;

    private boolean editMode = false;
    private Long editId = null;

    private String searchKeyword;

    @Init
    public void init() {}

    // Getter & Setter
    public List<User> getFilteredUsers() { return filteredUsers; }
    public String getNpk() { return npk; }
    public void setNpk(String npk) { this.npk = npk; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getPosisi() { return posisi; }
    public void setPosisi(String posisi) { this.posisi = posisi; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public AImage getImageMedia() { return imageMedia; }
    public void setImageMedia(AImage imageMedia) { this.imageMedia = imageMedia; }
    public String getFileLabel() { return fileLabel; }
    public void setFileLabel(String fileLabel) { this.fileLabel = fileLabel; }
    public boolean isEditMode() { return editMode; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
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
            Clients.showNotification("Access denied.", "error", null, "top_center", 2000);
            return;
        }

        if (npk == null || nama == null || posisi == null || status == null || (!editMode && password == null)) {
            Clients.showNotification("Semua field wajib diisi!", "warning", null, "top_center", 2000);
            return;
        }

        byte[] imgBytes = null;
        if (imageMedia != null) {
            try (InputStream is = imageMedia.getStreamData(); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] data = new byte[16384];
                int nRead;
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                imgBytes = buffer.toByteArray();
            } catch (IOException e) {
                Clients.showNotification("Gagal membaca gambar!", "error", null, "top_center", 2000);
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
        } else {
            user = new User();
            user.setNpk(npk);
            user.setNamaKaryawan(nama);
            user.setPosisi(posisi);
            user.setStatus(status);
            user.setPassword(passwordEncoder.encode(password));
            user.setImageData(imgBytes);

            // Role default USER
            Role defaultRole = roleService.findByName("USER");
            if (defaultRole != null) {
                Set<Role> roles = new HashSet<>();
                roles.add(defaultRole);
                user.setRoles(roles);
            }

            userService.addUser(user);
        }

        users = userService.getAllUsers();
        filteredUsers = new ArrayList<>(users);
        resetForm();
    }

    @Command
    @NotifyChange("*")
    public void edit(@BindingParam("user") User user, @BindingParam("index") int index) {
        if (!isAdmin()) {
            Clients.showNotification("Access denied.", "error", null, "top_center", 2000);
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
            Clients.showNotification("Access denied.", "error", null, "top_center", 2000);
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
            Clients.showNotification("User tidak ditemukan.", "error", null, "top_center", 2000);
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

    @GlobalCommand
    @NotifyChange({"imageMedia", "fileLabel"})
    public void uploadImage(@BindingParam("media") Media media) {
        System.out.println("UPLOAD IMAGE CALLED!");
        if (media == null) {
            Clients.showNotification("Media null", "error", null, "top_center", 3000);
            return;
        }

        if (!(media instanceof AImage)) {
            Clients.showNotification("File bukan gambar (.png/.jpg)", "error", null, "top_center", 3000);
            return;
        }

        try {
            this.imageMedia = (AImage) media;
            this.fileLabel = "Uploaded: " + media.getName() + " (" + (media.getByteData().length / 1024) + " KB)";
            Clients.showNotification("Upload berhasil!", "info", null, "top_center", 1500);
        } catch (Exception e) {
            e.printStackTrace();
            Clients.showNotification("Upload gagal: " + e.getMessage(), "error", null, "top_center", 3000);
        }

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
            ).toList();
        }
    }
}
