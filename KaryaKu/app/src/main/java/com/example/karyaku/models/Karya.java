package com.example.karyaku.models;

import java.io.Serializable;

public class Karya implements Serializable {

    private int id, userId;
    private String judul;
    private String deskripsi;
    private String gambar;
    private String tautan;
    private String gambarPath;
    private String gambarUri;
    private String createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }


    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getGambar() { return gambar; }
    public void setGambar(String gambar) { this.gambar = gambar; }

    public String getTautan() { return tautan; }
    public void setTautan(String tautan) { this.tautan = tautan; }

    public String getGambarPath() { return gambarPath; }
    public void setGambarPath(String gambarPath) { this.gambarPath = gambarPath; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
