package com.selimcinar.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Placeholder;
import androidx.recyclerview.widget.RecyclerView;

import com.selimcinar.myapplication.databinding.RecyclerRowBinding;
import com.selimcinar.myapplication.model.Place;
import com.selimcinar.myapplication.view.MapsActivity;

import java.io.Serializable;
import java.util.List;

public class PlaceAdapter  extends RecyclerView.Adapter<PlaceAdapter.Placeholder> {

    List<Place> placeList;
    public  PlaceAdapter (List<Place> placeList){
        this.placeList = placeList;
    }




    @NonNull
    @Override
    public Placeholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Place golder döndürme işlemi binding üzerinden  bağla ve görünümleri al
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new Placeholder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull Placeholder holder, int position) {
        //Görünümle adapter bağlanınca ne olacak onBindViewHolder
        //Recycler view içerisinde listede ekli ismi göster.
        holder.recyclerRowBinding.recyclerViewTextView.setText(placeList.get(position).name);
        //Tıklanınca görünüme ne olsun
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tıklandıktan sonra MapsActivity'ye geçiş yap
                Intent intent  = new Intent(holder.itemView.getContext(), MapsActivity.class);
                intent.putExtra("place",placeList.get(position));
                //Eski bir veri gönderiliyor mapsactiviye
                intent.putExtra("info","old");
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    //Kaç tane row oluşacağı
    @Override
    public int getItemCount() {
        return placeList.size();
    }

    //Place holderı binding ve viewBinding ile kullanılabilir hale getirme
    public  class  Placeholder extends RecyclerView.ViewHolder{
            //Constructor oluştu
        RecyclerRowBinding recyclerRowBinding;
        public Placeholder(@NonNull RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }


}
