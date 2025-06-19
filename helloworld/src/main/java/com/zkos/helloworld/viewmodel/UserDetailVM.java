package com.zkos.helloworld.viewmodel;

import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.image.Image;

public class UserDetailVM {
    private String npk;
    private String nama;
    private String posisi;
    private String status;
    private Image imageMedia;

    @Init
    public void init(@ExecutionArgParam("npk") String npk,
                     @ExecutionArgParam("nama") String nama,
                     @ExecutionArgParam("posisi") String posisi,
                     @ExecutionArgParam("status") String status,
                     @ExecutionArgParam("imageMedia") Image imageMedia) {
        this.npk = npk;
        this.nama = nama;
        this.posisi = posisi;
        this.status = status;
        this.imageMedia = imageMedia;
    }

    public String getNpk() { return npk; }
    public String getNama() { return nama; }
    public String getPosisi() { return posisi; }
    public String getStatus() { return status; }
    public Image getImageMedia() { return imageMedia; }
}
