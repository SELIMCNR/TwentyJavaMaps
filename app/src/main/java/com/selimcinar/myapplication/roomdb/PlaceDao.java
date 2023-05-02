package com.selimcinar.myapplication.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.selimcinar.myapplication.model.Place;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;


@Dao
public interface PlaceDao  {
    @Query("Select * from Place")
    Flowable<List <Place>> getAll();
    // sql cümlesine göre
        //List<Place> getAll();  // geriye bir liste dönder.
    //Flowable rxj javadan gelme terim.Flowable döner değer.
    /*@Query("Select * from place Where name =:nameInput ") // sql cümlesine göre dışardan parametre al ve filtrele
    List<Place> getAll(String nameInput);  // Parametrede değer ala ve listeye dönüştür işlemleri.
*/
    @Insert  // ekleme işlemi sadece işlemin yapılacağı sınıfı modeli ver.
    Completable insert (Place place); //Completable rx javadan gelme terim rx.java arka planda işlemleri gerçekleştirir.
    @Delete // Silme işlemi metot içinde model yada sınıfı ver ve işlemi yap.
    Completable delete(Place place);

    //List<Place> getAll(String nameInput);
}

