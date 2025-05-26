package com.example.karyaku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.karyaku.adapters.KaryaAdapter;
import com.example.karyaku.models.Karya;

import java.util.ArrayList;
import com.example.karyaku.utils.DBHelper;

public class DashboardActivity extends AppCompatActivity{

    ListView listViewKarya;
    Button btnTambahKarya;
    DBHelper dbHelper;
    ArrayList<Karya> daftarKarya;
    KaryaAdapter karyaAdapter;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        listViewKarya = findViewById(R.id.listViewKarya);
        btnTambahKarya = findViewById(R.id.btnTambahKarya);

        dbHelper = new DBHelper(this);

        daftarKarya = dbHelper.getAllKarya();
        karyaAdapter = new KaryaAdapter(this, daftarKarya);
        listViewKarya.setAdapter(karyaAdapter);

        listViewKarya.setOnItemClickListener((adapterView, view, i, l) -> {
            Karya selectedKarya = daftarKarya.get(i);
            Intent intent = new Intent(DashboardActivity.this, DetailKaryaActivity.class);
            intent.putExtra("karya_id", selectedKarya.getId());
            startActivity(intent);
        });

        btnTambahKarya.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddKaryaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        daftarKarya = dbHelper.getAllKarya();
        karyaAdapter = new KaryaAdapter(this, daftarKarya);
        listViewKarya.setAdapter(karyaAdapter);
    }
}
