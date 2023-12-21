package com.selimcinar.myapplication.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity  // roomdb'yi kullanacam demek  istenirse varsayılan tablo adıda verilebilir @Entity(tableName = "asd")

public class Place implements Serializable{
    //room dbli veritabanı oluşturma işlemleri
    @PrimaryKey(autoGenerate = true) // özel anahtarlı id oluştur
    public  int id ;

    @ColumnInfo(name= "name") // kolon ismi 
    public  String name;

    @ColumnInfo(name = "latiude") //kolon ismi
    public Double latiude;
    @ColumnInfo(name = "longitude") //kolon ismi
    public Double longitude;

    //Place modelinin constructoru
    public  Place (String name,Double latiude,Double longitude){
        this.name = name;
        this.latiude=latiude;
        this.longitude = longitude;
    }
}
