package com.example.karyaku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.karyaku.utils.DBHelper;
import com.example.karyaku.utils.FileUtils;
import android.Manifest;
import android.content.pm.PackageManager;

public class AddKaryaActivity extends AppCompatActivity {

    private EditText etJudul, etDeskripsi, etTautan;
    private ImageView ivPreview;
    private Button btnUpload, btnSimpan;
    private String gambarPath = "";

    private static final int REQUEST_GALLERY = 100;
    private static final int REQUEST_CAMERA = 200;
    private static final int REQUEST_PERMISSION = 300;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_karya);

        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etTautan = findViewById(R.id.etTautan);
        ivPreview = findViewById(R.id.ivPreview);
        btnUpload = findViewById(R.id.btnUploadGambar);
        btnSimpan = findViewById(R.id.btnSimpanKarya);

        dbHelper = new DBHelper(this);

        btnUpload.setOnClickListener(v -> {
            showImagePickerOptions();
        });

        btnSimpan.setOnClickListener(v -> {
            simpanKarya();
        });

        // Minta izin runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }

    private void showImagePickerOptions() {
        String[] options = {"Pilih dari Galeri", "Ambil dari Kamera"};
        new AlertDialog.Builder(this)
                .setTitle("Pilih Gambar")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickIntent, REQUEST_GALLERY);
                    } else {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, REQUEST_CAMERA);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_GALLERY && data.getData() != null) {
                Uri uri = data.getData();
                ivPreview.setImageURI(uri);
                gambarPath = FileUtils.getPath(this, uri);
            } else if (requestCode == REQUEST_CAMERA && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivPreview.setImageBitmap(bitmap);

                try {
                    File file = File.createTempFile("karya_", ".jpg", getExternalFilesDir(null));
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                    gambarPath = file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void simpanKarya() {
        String judul = etJudul.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();
        String tautan = etTautan.getText().toString().trim();

        if (judul.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Judul dan Deskripsi wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean sukses = dbHelper.insertKarya(judul, deskripsi, gambarPath, tautan);
        if (sukses) {
            Toast.makeText(this, "Karya berhasil disimpan", Toast.LENGTH_SHORT).show();
            finish(); // kembali ke dashboard
        } else {
            Toast.makeText(this, "Gagal menyimpan karya", Toast.LENGTH_SHORT).show();
        }
    }
}

