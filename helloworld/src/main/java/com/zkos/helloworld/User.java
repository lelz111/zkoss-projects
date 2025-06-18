package com.zkos.helloworld;

import org.zkoss.image.Image;

public class User {
    private String npk;
    private String namaKaryawan;
    private String posisi;
    private String status;
    private Image imageMedia;

    public User() {}

    public User(String npk, String namaKaryawan, String posisi, String status, Image imageMedia) {
        this.npk = npk;
        this.namaKaryawan = namaKaryawan;
        this.posisi = posisi;
        this.status = status;
        this.imageMedia = imageMedia;
    }

    public String getNpk() {
        return npk;
    }

    public void setNpk(String npk) {
        this.npk = npk;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Image getImageMedia() {
        return imageMedia;
    }

    public void setImageMedia(Image imageMedia) {
        this.imageMedia = imageMedia;
    }
}
