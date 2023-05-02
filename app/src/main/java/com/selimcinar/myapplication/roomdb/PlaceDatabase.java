package com.selimcinar.myapplication.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.selimcinar.myapplication.model.Place;



//Modelleri buraya ekle ve veritabanını oluşturma işlemi.
//version entitieste değişiklik yapılırsa artırılır.
@Database(entities = {Place.class},version = 1)
public abstract class PlaceDatabase extends RoomDatabase {
    public abstract PlaceDao placeDao();
}
