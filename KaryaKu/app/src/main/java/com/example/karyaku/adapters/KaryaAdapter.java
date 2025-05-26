package com.example.karyaku.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.karyaku.DetailKaryaActivity;
import com.example.karyaku.R;
import com.example.karyaku.models.Karya;
import java.util.ArrayList;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class KaryaAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Karya> karyaList;
    private LayoutInflater inflater;

    public KaryaAdapter(Context context, ArrayList<Karya> karyaList){
        this.context = context;
        this.karyaList = karyaList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return karyaList.size();
    }

    @Override
    public Object getItem(int i) {
        return karyaList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return karyaList.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_karya, parent, false);
            holder = new ViewHolder();
            holder.ivGambar = convertView.findViewById(R.id.ivGambarKarya);
            holder.tvJudul = convertView.findViewById(R.id.tvJudulKarya);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Karya karya = karyaList.get(position);

        holder.tvJudul.setText(karya.getJudul());

        if (karya.getGambarPath() != null && !karya.getGambarPath().isEmpty()) {
            holder.ivGambar.setImageBitmap(BitmapFactory.decodeFile(karya.getGambarPath()));
        } else {
            holder.ivGambar.setImageResource(R.drawable.placeholder_image); // placeholder jika tidak ada gambar
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailKaryaActivity.class);
                intent.putExtra("id", karya.getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivGambar;
        TextView tvJudul;
    }
}
