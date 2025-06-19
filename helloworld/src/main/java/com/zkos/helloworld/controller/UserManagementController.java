package com.zkos.helloworld.controller;

import com.zkos.helloworld.model.User;
import com.zkos.helloworld.service.UserService;
import com.zkos.helloworld.service.UserServiceImpl;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManagementController extends SelectorComposer<Component> {
    @Wire private Listbox userListbox;
    @Wire private Textbox npkBox, namaBox, posisiBox, statusBox, searchBox;
    @Wire private Image userImage;
    @Wire private Label fileLabel;

    private final UserService userService = new UserServiceImpl();
    private ListModelList<User> model;
    private Media uploadedMedia;
    private int editIndex = -1;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        // Tampilkan kosong dulu
        model = new ListModelList<>();
        userListbox.setModel(model);

        userListbox.setItemRenderer((Listitem listitem, User user, int index) -> {
            listitem.appendChild(new Listcell(user.getNpk()));
            listitem.appendChild(new Listcell(user.getNamaKaryawan()));
            listitem.appendChild(new Listcell(user.getPosisi()));
            listitem.appendChild(new Listcell(user.getStatus()));

            Listcell imgCell = new Listcell();
            if (user.getImageMedia() != null) {
                Image img = new Image();
                img.setContent(user.getImageMedia());
                img.setWidth("40px");
                img.setHeight("40px");
                imgCell.appendChild(img);
            }
            listitem.appendChild(imgCell);

            Listcell actionCell = new Listcell();
            Hbox actionBox = new Hbox();
            actionBox.setSpacing("5px");

            Button editBtn = new Button("Edit");
            Button delBtn = new Button("Delete");
            Button viewBtn = new Button("View"); // ✅ tombol baru

            editBtn.addEventListener("onClick", e -> editUser(index));
            delBtn.addEventListener("onClick", e -> deleteUser(index));
            viewBtn.addEventListener("onClick", e -> viewUserDetail(user));


            actionBox.appendChild(editBtn);
            actionBox.appendChild(delBtn);
            actionBox.appendChild(viewBtn); // ✅ tambahkan tombol
            actionCell.appendChild(actionBox);
            listitem.appendChild(actionCell);
        });

    }

    @Listen("onClick = #searchBtn")
    public void searchUsers() {
        String keyword = searchBox.getValue().toLowerCase();

        List<User> filtered = userService.getAllUsers().stream()
                .filter(u -> u.getNpk().toLowerCase().contains(keyword)
                        || u.getNamaKaryawan().toLowerCase().contains(keyword)
                        || u.getPosisi().toLowerCase().contains(keyword)
                        || u.getStatus().toLowerCase().contains(keyword))
                .toList();

        model.clear();
        model.addAll(filtered);
    }

    @Listen("onClick = #resetBtn")
    public void resetSearch() {
        searchBox.setValue("");
        model.clear();
    }

    @Listen("onClick = #saveBtn")
    public void saveUser() throws IOException {
        String npk = npkBox.getValue();
        String nama = namaBox.getValue();
        String posisi = posisiBox.getValue();
        String status = statusBox.getValue();
        AImage img = uploadedMedia != null ? new AImage(uploadedMedia.getName(), uploadedMedia.getStreamData()) : null;

        User user = new User(npk, nama, posisi, status, img);

        if (editIndex >= 0) {
            userService.updateUser(editIndex, user);
            model.set(editIndex, user);
            editIndex = -1;
        } else {
            userService.addUser(user);
            model.add(user);
        }

        clearForm();
    }

    @Listen("onClick = #cancelBtn")
    public void cancelEdit() {
        clearForm();
    }

    @Listen("onUpload = #uploadBtn")
    public void uploadImage(UploadEvent event) {
        uploadedMedia = event.getMedia();
        fileLabel.setValue(uploadedMedia.getName());
        try {
            userImage.setContent(new AImage(uploadedMedia.getName(), uploadedMedia.getStreamData()));
        } catch (IOException e) {
            Messagebox.show("Gagal menampilkan gambar");
        }
    }

    @GlobalCommand("refreshUserList")
    public void refreshUserList(@BindingParam("index") int index,
                                @BindingParam("user") User updatedUser) {
        // Update data di list
        model.set(index, updatedUser); // ✅ ini akan trigger UI update pada listbox
        System.out.println("Updated user at index " + index + ": " + updatedUser.getNamaKaryawan());

    }



    private void viewUserDetail(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("npk", user.getNpk());
        params.put("nama", user.getNamaKaryawan());
        params.put("posisi", user.getPosisi());
        params.put("status", user.getStatus());
        params.put("imageMedia", user.getImageMedia()); // ✅ Tambahkan ini

        Window win = (Window) Executions.createComponents("/user_detail.zul", null, params);
        win.setClosable(true);
        win.setTitle("Detail User");
        win.doModal();
    }

    private void editUser(int index) {
        User user = model.get(index);

        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("index", index); // ✅ Kirim index

        Window win = (Window) Executions.createComponents("/user_edit.zul", null, params);
        win.setTitle("Edit User");
        win.setClosable(true);
        win.doModal();
    }




    private void deleteUser(int index) {
        if (Messagebox.show("Hapus data ini?", "Konfirmasi", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
            User deleted = model.remove(index);
            userService.getAllUsers().remove(deleted); // sinkron dengan service
        }
    }

    private void clearForm() {
        npkBox.setValue("");
        namaBox.setValue("");
        posisiBox.setValue("");
        statusBox.setValue("");
        userImage.setSrc("");
        uploadedMedia = null;
        fileLabel.setValue("");
    }

}
