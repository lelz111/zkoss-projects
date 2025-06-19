package com.zkos.helloworld.viewmodel;

import com.zkos.helloworld.model.User;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import java.util.HashMap;
import java.util.Map;

public class UserEditVM {

    private User user;
    private Window editWin;
    private int index;


    public String getNpk() { return user.getNpk(); }
    public void setNpk(String npk) { user.setNpk(npk); }

    public String getNama() { return user.getNamaKaryawan(); }
    public void setNama(String nama) { user.setNamaKaryawan(nama); }

    public String getPosisi() { return user.getPosisi(); }
    public void setPosisi(String posisi) { user.setPosisi(posisi); }

    public String getStatus() { return user.getStatus(); }
    public void setStatus(String status) { user.setStatus(status); }

    @Init
    public void init(@ContextParam(ContextType.VIEW) Component view,
                     @ExecutionArgParam("user") User user,
                     @ExecutionArgParam("index") int index) {
        this.editWin = (Window) view;
        this.user = user;
        this.index = index;
    }


    @Command
    public void save() {
        Clients.showNotification("Data berhasil diperbarui!", "info", null, "middle_center", 2000);
        editWin.detach();

        // Buat salinan data baru agar dianggap 'berubah'
        User updatedUser = new User(user.getNpk(), user.getNamaKaryawan(), user.getPosisi(), user.getStatus(), user.getImageMedia());

        Map<String, Object> args = new HashMap<>();
        args.put("index", index);
        args.put("user", updatedUser); // ← KIRIM objek baru, bukan referensi lama

        BindUtils.postGlobalCommand(null, null, "refreshUserList", args);
    }


    @Command
    public void cancel() {
        editWin.detach();
    }

}
