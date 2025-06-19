package com.zkos.helloworld.viewmodel;

import com.zkos.helloworld.model.User;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.Image;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import java.util.HashMap;

public class UserEditVM {
    private User user;
    private Window win;
    private String npk, nama, posisi, status;

    @Init
    public void init(@ContextParam(ContextType.VIEW) Component view,
                     @ExecutionArgParam("user") User u) {
        this.win = (Window) view;
        this.user = u;
        this.npk = u.getNpk(); this.nama = u.getNamaKaryawan();
        this.posisi = u.getPosisi(); this.status = u.getStatus();
    }

    public String getNpk(){return npk;} public void setNpk(String s){npk=s;}
    public String getNama(){return nama;} public void setNama(String s){nama=s;}
    public String getPosisi(){return posisi;} public void setPosisi(String s){posisi=s;}
    public String getStatus(){return status;} public void setStatus(String s){status=s;}

    @Command
    public void save() {
        user.setNpk(npk); user.setNamaKaryawan(nama);
        user.setPosisi(posisi); user.setStatus(status);
        Clients.showNotification("Data saved!", "info", null, "middle_center", 1500);
        win.detach();
        BindUtils.postGlobalCommand(null, null, "afterEdit", null);
    }

    @Command
    public void cancel() {
        win.detach();
    }
}
