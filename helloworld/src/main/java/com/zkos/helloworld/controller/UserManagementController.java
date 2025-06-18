package com.zkos.helloworld.controller;

import com.zkos.helloworld.model.User;
import com.zkos.helloworld.service.UserService;
import com.zkos.helloworld.service.UserServiceImpl;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.List;

public class UserManagementController extends SelectorComposer<Component> {
    @Wire private Listbox userListbox;
    @Wire private Textbox npkBox, namaBox, posisiBox, statusBox;
    @Wire private Image userImage;
    @Wire private Label fileLabel;

    private final UserService userService = new UserServiceImpl();
    private ListModelList<User> model;
    private Media uploadedMedia;
    private int editIndex = -1;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        model = new ListModelList<>(userService.getAllUsers());
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
            Button editBtn = new Button("Edit");
            Button delBtn = new Button("Delete");

            editBtn.addEventListener("onClick", e -> editUser(index));
            delBtn.addEventListener("onClick", e -> deleteUser(index));

            actionBox.appendChild(editBtn);
            actionBox.appendChild(delBtn);
            actionCell.appendChild(actionBox);
            listitem.appendChild(actionCell);
        });
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

    private void editUser(int index) {
        User user = userService.getUser(index);
        npkBox.setValue(user.getNpk());
        namaBox.setValue(user.getNamaKaryawan());
        posisiBox.setValue(user.getPosisi());
        statusBox.setValue(user.getStatus());
        userImage.setContent(user.getImageMedia());
        uploadedMedia = user.getImageMedia(); // supaya bisa simpan ulang
        editIndex = index;
    }

    private void deleteUser(int index) {
        if (Messagebox.show("Hapus data ini?", "Konfirmasi", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
            userService.deleteUser(index);
            model.remove(index);
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
