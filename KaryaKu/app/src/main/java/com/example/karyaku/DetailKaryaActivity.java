package com.example.karyaku;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.karyaku.models.Karya;
import com.example.karyaku.utils.DBHelper;

import java.util.List;


public class DetailKaryaActivity extends AppCompatActivity {

    TextView tvJudul, tvDeskripsi, tvTautan;
    ImageView ivGambar, btnLike, btnDislike, btnShare, btnEdit, btnHapus;

    DBHelper dbHelper;
    Karya karya;

    EditText etKomentar;
    Button btnKirimKomentar;
    LinearLayout komentarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_karya);

        dbHelper = new DBHelper(this);

        // Inisialisasi view
        tvJudul = findViewById(R.id.tvJudul);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvTautan = findViewById(R.id.tvTautan);
        ivGambar = findViewById(R.id.ivGambar);

        btnLike = findViewById(R.id.btnLike);
        btnDislike = findViewById(R.id.btnDislike);
        btnShare = findViewById(R.id.btnShare);
        btnEdit = findViewById(R.id.btnEdit);
        btnHapus = findViewById(R.id.btnHapus);
        etKomentar = findViewById(R.id.etKomentar);
        btnKirimKomentar = findViewById(R.id.btnKirimKomentar);
        komentarContainer = findViewById(R.id.komentarContainer);

// Ambil data dari intent
        int idKarya = getIntent().getIntExtra("id", -1);
        karya = (Karya) dbHelper.getKaryaObjectById(idKarya);
        if (karya == null) {
            Toast.makeText(this, "Karya tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish(); // keluar dari activity agar tidak error
            return;
        }
        tvJudul.setText(karya.getJudul());
        tvDeskripsi.setText(karya.getDeskripsi());
        tvTautan.setText(karya.getTautan());

        Glide.with(this)
                .load(karya.getGambarPath())
                .into(ivGambar);

        loadKomentar(komentarContainer);
        btnKirimKomentar.setOnClickListener(v -> {
            String teks = etKomentar.getText().toString().trim();
            if (!teks.isEmpty()) {
                dbHelper.insertKomentar(karya.getId(), teks);
                etKomentar.setText("");
                loadKomentar(komentarContainer);
            }
        });

        btnLike.setOnClickListener(v -> {
            Toast.makeText(this, "Kamu menyukai karya ini.", Toast.LENGTH_SHORT).show();
        });

        btnDislike.setOnClickListener(v -> {
            Toast.makeText(this, "Kamu tidak menyukai karya ini.", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareText = karya.getJudul() + "\n" + karya.getTautan();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Bagikan karya via"));
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditKaryaActivity.class);
            intent.putExtra("id", karya.getId());
            startActivity(intent);
        });

        btnHapus.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Karya")
                    .setMessage("Yakin ingin menghapus karya ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        dbHelper.deleteKarya(karya.getId());
                        Toast.makeText(this, "Karya berhasil dihapus", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void loadKomentar(LinearLayout container) {
        container.removeAllViews();
        List<String> komentarList = dbHelper.getKomentarByKaryaId(karya.getId());

        for (String komentar : komentarList) {
            TextView tv = new TextView(this);
            tv.setText(komentar);
            tv.setPadding(8, 8, 8, 8);
            container.addView(tv);
        }
    }
}