package com.zkos.helloworld.model;

//import jakarta.persistence.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "users") // nama tabel di DB
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key

    @Column(nullable = false, unique = true)
    private String npk;

    @Column(nullable = false)
    private String namaKaryawan;

    @Column(nullable = false)
    private String posisi;

    @Column(nullable = false)
    private String status;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData; // representasi binary dari image

    // Constructors
    public User() {}

    public User(String npk, String namaKaryawan, String posisi, String status, byte[] imageData) {
        this.npk = npk;
        this.namaKaryawan = namaKaryawan;
        this.posisi = posisi;
        this.status = status;
        this.imageData = imageData;
    }

    // Getters and Setters
    public Long getId() {
        return id;
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

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    // hashCode & equals based on id
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return Objects.equals(id, other.id);
    }

    // Optional: toString()
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", npk='" + npk + '\'' +
                ", namaKaryawan='" + namaKaryawan + '\'' +
                ", posisi='" + posisi + '\'' +
                ", status='" + status + '\'' +
                ", imageData=" + (imageData != null ? imageData.length + " bytes" : "null") +
                '}';
    }
}
