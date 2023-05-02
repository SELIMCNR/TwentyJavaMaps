package com.selimcinar.myapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.selimcinar.myapplication.R;
import com.selimcinar.myapplication.adapter.PlaceAdapter;
import com.selimcinar.myapplication.databinding.ActivityMainBinding;
import com.selimcinar.myapplication.databinding.ActivityMapsBinding;
import com.selimcinar.myapplication.model.Place;
import com.selimcinar.myapplication.roomdb.PlaceDao;
import com.selimcinar.myapplication.roomdb.PlaceDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    //CompositeDisposable kullan at oluştur
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
   ArrayList<Place> places;
    PlaceDatabase db;
    PlaceDao placeDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        places = new ArrayList<>();
        //Places adlı database inşa et
        db = Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places").build();
        //Database içerisinde entity modele eriş
        placeDao = db.placeDao();

        //Kullan at yapısı ile disposable ile
        /*Bellekte geçici süre ile tut javanın io'su içerisinde işleme başla android iosunda da mainThread arka plan işlemlerini yap
         Listeyi flowable olarak ver ve handle response metodu içerisindekileri uygula*/

        compositeDisposable.add(placeDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                MainActivity.this::handleResponse)
        );



    }

    //Liste şeklinde değerleri ver (List<Place> placeList) flowable da liste olarak aldığın için  Flowable<List <Place>> getAll();
    private  void handleResponse (List<Place> placeList){
        //Alt alta listeyi koy
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Adapterı tanımla ve placeList'i metot olarak ver
        PlaceAdapter placeAdapter = new PlaceAdapter(placeList);
        //recycler viewa adapterı ekle
        binding.recyclerView.setAdapter(placeAdapter);
    }


    //onCreateOptionMenu  Seçenek menusu olusunca menuInflater ile  R.menu.travel_menu'ye  bağlan.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //Seçenek menusu olusunca menuInflater ile  R.menu.travel_menu'ye  bağlan
        menuInflater.inflate(R.menu.travel_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    //onOptionsItemsSelected menuden birşey seçilirse ne olacak
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Menuye tıklanınca diğer aktivitiye geçiş yap .
        if (item.getItemId() == R.id.add_place) {
            Intent intent = new Intent(this,MapsActivity.class);
            //Yeni bir veri gönderildi MapsActiviye
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}