package com.example.karyaku;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.karyaku.utils.DBHelper;
import com.example.karyaku.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class EditKaryaActivity extends AppCompatActivity{

    EditText etJudul, etDeskripsi, etTautan;
    ImageView imagePreview;
    Button btnPilihGambar, btnSimpanPerubahan;
    DBHelper dbHelper;
    int karyaId;
    String gambarPath;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_karya);

        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etTautan = findViewById(R.id.etTautan);
        imagePreview = findViewById(R.id.imagePreview);
        btnPilihGambar = findViewById(R.id.btnPilihGambar);
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan);
        dbHelper = new DBHelper(this);

        karyaId = getIntent().getIntExtra("karya_id", -1);

        if (karyaId != -1) {
            loadDataKarya(karyaId);
        }

        btnPilihGambar.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, PICK_IMAGE);
        });

        btnSimpanPerubahan.setOnClickListener(v -> {
            String judul = etJudul.getText().toString();
            String deskripsi = etDeskripsi.getText().toString();
            String tautan = etTautan.getText().toString();

            dbHelper.updateKarya(karyaId, judul, deskripsi, gambarPath, tautan);
            Toast.makeText(this, "Karya berhasil diupdate", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    private void loadDataKarya(int id) {
        Cursor cursor = dbHelper.getKaryaById(id);
        if (cursor.moveToFirst()) {
            etJudul.setText(cursor.getString(cursor.getColumnIndex("judul")));
            etDeskripsi.setText(cursor.getString(cursor.getColumnIndex("deskripsi")));
            etTautan.setText(cursor.getString(cursor.getColumnIndex("tautan")));
            gambarPath = cursor.getString(cursor.getColumnIndex("gambar"));

            if (gambarPath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(gambarPath);
                imagePreview.setImageBitmap(bitmap);
            }
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imagePreview.setImageBitmap(bitmap);
                gambarPath = FileUtils.getPath(this, imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
