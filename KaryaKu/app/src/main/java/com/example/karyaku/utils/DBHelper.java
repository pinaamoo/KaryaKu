package com.example.karyaku.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.karyaku.models.Karya;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "karyaku.db";
    public static final int DATABASE_VERSION = 1;

    private static final String TABLE_KARYA = "karya";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_JUDUL = "judul";
    private static final String COLUMN_DESKRIPSI = "deskripsi";
    private static final String COLUMN_GAMBAR = "gambar";
    private static final String COLUMN_TAUTAN = "tautan";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabel user
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, email TEXT, password TEXT)");

        // Tabel karya
        db.execSQL("CREATE TABLE karya (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "judul TEXT, deskripsi TEXT, gambar TEXT, tautan TEXT, created_at TEXT)");

        // Tabel komentar
        db.execSQL("CREATE TABLE komentar (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "karya_id INTEGER, user_id INTEGER, komentar TEXT, created_at TEXT)");

        // Tabel like/dislike
        db.execSQL("CREATE TABLE likes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "karya_id INTEGER, user_id INTEGER, type TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS karya");
        db.execSQL("DROP TABLE IF EXISTS komentar");
        db.execSQL("DROP TABLE IF EXISTS likes");
        onCreate(db);
    }

    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public Cursor loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
    }

    public ArrayList<Karya> getAllKarya() {
        List<Karya> karyaList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_KARYA + " ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Karya karya = new Karya();
                karya.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                karya.setJudul(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JUDUL)));
                karya.setDeskripsi(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESKRIPSI)));
                karya.setGambar(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR)));
                karya.setTautan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAUTAN)));
                karyaList.add(karya);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return (ArrayList<Karya>) karyaList;
    }

    public boolean insertKarya(String judul, String deskripsi, String gambar, String tautan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_JUDUL, judul);
        values.put(COLUMN_DESKRIPSI, deskripsi);
        values.put(COLUMN_GAMBAR, gambar);
        values.put(COLUMN_TAUTAN, tautan);

        long result = db.insert(TABLE_KARYA, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getKaryaById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM karya WHERE id = ?", new String[]{String.valueOf(id)});
    }

    public Karya getKaryaObjectById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM karya WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            Karya karya = new Karya();
            karya.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            karya.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            karya.setJudul(cursor.getString(cursor.getColumnIndexOrThrow("judul")));
            karya.setDeskripsi(cursor.getString(cursor.getColumnIndexOrThrow("deskripsi")));
            karya.setGambar(cursor.getString(cursor.getColumnIndexOrThrow("gambar")));
            karya.setTautan(cursor.getString(cursor.getColumnIndexOrThrow("tautan")));
            karya.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));

            // Opsional: gunakan setGambarPath jika ingin memakai path untuk Glide
            karya.setGambarPath(cursor.getString(cursor.getColumnIndexOrThrow("gambar")));

            cursor.close();
            return karya;
        }
        return null;
    }


    public boolean updateLikeDislike(int karyaId, String tipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        String kolom = tipe.equals("like") ? "like_count" : "dislike_count";
        db.execSQL("UPDATE karya SET " + kolom + " = " + kolom + " + 1 WHERE id = ?", new Object[]{karyaId});
        return true;
    }

    public ArrayList<String> getKomentarByKaryaId(int karyaId) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM komentar WHERE karya_id = ? ORDER BY created_at DESC",
                new String[]{String.valueOf(karyaId)});
        if (cursor.moveToFirst()) {
            do {
                String isi = cursor.getString(cursor.getColumnIndexOrThrow("isi"));
                list.add(isi);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean deleteKarya(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("karya", "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public void insertKomentar(int karyaId, String komentar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("karya_id", karyaId);
        values.put("komentar", komentar);
        values.put("created_at", String.valueOf(System.currentTimeMillis()));
        db.insert("komentar", null, values);
    }

    public void updateKarya(int id, String judul, String deskripsi, String gambar, String tautan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("judul", judul);
        values.put("deskripsi", deskripsi);
        values.put("gambar", gambar);
        values.put("tautan", tautan);
        db.update("karya", values, "id=?", new String[]{String.valueOf(id)});
    }
}