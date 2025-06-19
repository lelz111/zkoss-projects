package com.zkos.helloworld.viewmodel;

import com.zkos.helloworld.model.User;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.image.AImage;
import org.zkoss.image.Image;

import java.io.IOException;

public class UserDetailVM {
    private String npk, nama, posisi, status;
    private Image imageMedia;

    @Init
    public void init(@ExecutionArgParam("user") User u) {
        this.npk = u.getNpk();
        this.nama = u.getNamaKaryawan();
        this.posisi = u.getPosisi();
        this.status = u.getStatus();
        try {
            this.imageMedia = u.getImageData() != null ? new AImage("img", u.getImageData()) : null;
        } catch (IOException e) {
            this.imageMedia = null;
        }
    }

    public String getNpk() { return npk; }
    public String getNama() { return nama; }
    public String getPosisi() { return posisi; }
    public String getStatus() { return status; }
    public Image getImageMedia() { return imageMedia; }
}
