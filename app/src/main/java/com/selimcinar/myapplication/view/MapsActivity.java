package com.selimcinar.myapplication.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;
import androidx.room.RoomSQLiteQuery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.selimcinar.myapplication.R;
import com.selimcinar.myapplication.databinding.ActivityMapsBinding;
import com.selimcinar.myapplication.model.Place;
import com.selimcinar.myapplication.roomdb.PlaceDao;
import com.selimcinar.myapplication.roomdb.PlaceDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

//İkinci arayüz eklendi GoogleMap.OnMapLongClickListener uzun tıklanınca ne olsun
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //izin alma işleminde kullanıldı activity çağrıldığında sonra ne olsun. ActivityResultLauncher
    ActivityResultLauncher<String> permissionLauncher;

    LocationManager locationManager;
    LocationListener locationListener;

    SharedPreferences sharedPreferences;
    boolean info  ;
    PlaceDatabase db; //db eklendi database
    PlaceDao placeDao; // dao eklendi database nesneleri modeli

    Double selectedLatitude;
    Double selectedLongitude;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    Place selectedPlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher();

        sharedPreferences = this.getSharedPreferences("com.selimcinar.myapplication",MODE_PRIVATE);
        info = false;

        //Places adlı database inşa et
        // .allowMainThreadQueries() ile arka planda yapılacak işlemleri ön planda geçici olarak yapabilin ama tavsiye edilmez. Çünkü büyük verilerde hata verir. Küçük verilerde kullanılabilir.
        //.allowMainThreadQueries()
        binding.saveButton.setEnabled(false);
        //Places adlı database inşa et
        db = Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places")
    .build();
        //Database içerisinde abstractClass olan placedaoyu çalıştır.
        placeDao = db.placeDao();

        selectedLatitude =0.0;
        selectedLongitude=0.0;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    /*on Map Ready harita hazır olunca ne olsun . */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //googleMap'i mMap'e eşitle
        mMap = googleMap;
        //uzun tıklama işini harita açılınca yap arayüzün this yani activiyinin kendi setOnMapLongClickListener
        mMap.setOnMapLongClickListener(this);

        //Intent oluştur ve veriyi al
        Intent intent = getIntent();
        String intentInfo = intent.getStringExtra("info");

        if (intentInfo.equals("new")) {
            //gelen veri new ise
            binding.saveButton.setVisibility(View.VISIBLE); //Kaydet buttonunu göster
            binding.deleteButton.setVisibility(View.GONE); //Silme buttonunu komple kaldır.

            /*LocationManager lokasyon yöneticisi (cast işlemi) bu locationManager döndürecek*/
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //LocationListener interface arayüz dinle lokasyonu al ve işlem yap
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    System.out.println("Location "+ location.toString());

                    //Son konumdan değer gelmemişse bu işlemi yap
                    info = sharedPreferences.getBoolean("info",false);
                    if (!info){
                        //Konumu al telefondan ve map içinde oraya git
                        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        // map ile gidilen yere zoom ile yaklaş
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        sharedPreferences.edit().putBoolean("info",true).apply();
                    }

                }
            };
            /*Acces _Fıne_Location dangerous tehlikeli izin olduğu için sadece manifeste yazmak yeterli değil*/
            //Konum almak için kullanıcıdan izin alınıyor
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {  // request permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Snacbar eklendi view olara binding , text olarak yazı , kulanıcı tıklanana kadar beklesin . aksiyon ata ne olsun
                    Snackbar.make(binding.getRoot(),"Permission needed for maps",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //tıklanınca izin istesin
                            //request permission izin verildiyse izinAçıcıyı aç ve lokasyon izni ver
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show(); // snackbarı göster.
                }else {
                    //request permission izinverildi izinAçıcıyı aç ve lokasyon izni ver
                }          permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }else {
                //Kullanıcıdan izin alındı
                //Konumu guncelleme kodları
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,100,locationListener);

                //Son konumu gösterme
                Location lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastlocation != null){
                    //Son konumu al kullanıcıdan
                    LatLng lastUserLocation = new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                    //alınan konuma zoom ile yaklaş
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                }
              //  mMap.setMyLocationEnabled(true); // benim lokasyonu göster yada emin ol orada olduğumdan
            }
            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,100,locationListener);
        }
         else {
             //sql verisi ve intent verisi gelir.
            mMap.clear();
            selectedPlace = (Place) intent.getSerializableExtra("place");
            LatLng latLng = new LatLng(selectedPlace.latiude,selectedPlace.longitude);
            mMap.addMarker(new MarkerOptions().position(latLng).title(selectedPlace.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

            binding.placeNameText.setText(selectedPlace.name);
            binding.saveButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.VISIBLE);

         }

    }



    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear(); //yeni uzun tıklamada eski markerı sil
        //Uzun tıklama işlemi sonrasında Marker ekle
        mMap.addMarker(new MarkerOptions().position(latLng));

        //Alınan konumları değişkene eşitle
        selectedLatitude = latLng.latitude;
        selectedLongitude=latLng.longitude;

        binding.saveButton.setEnabled(true);
    }

    private  void handleResponse(){

        //Main activity'e geçiş yap
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public  void  save(View view){
        //dışardan girilen değerleri al
        Place place = new Place(binding.placeNameText.getText().toString(),selectedLatitude,selectedLongitude);

        //Threading ve thread çeşitleri -> Main (Ui) , Default (Cpu Intensive,Arka planda yoğun işlem),IO (network,database) ağ ve veritabanı işlemleri yapılır.
        //placeDao.insert(place); // veritabanına değerleri ekle
        placeDao.insert(place).subscribeOn(Schedulers.io()).subscribe();

        //Disposable  kullan at
        //subscribeOn  işlemi yap bitir
        //observeOn  gözlemle
        compositeDisposable.add(placeDao.insert(place).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe( MapsActivity.this::handleResponse)); //Yapılcak diğer işlem :: handle response
    }
    public void  delete(View view){

        if (selectedPlace != null){
            compositeDisposable.add(placeDao.delete(selectedPlace).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe( MapsActivity.this::handleResponse));

        }



    }
    private  void registerLauncher(){
        //Aktivitesonradan başlayınca izin alma contratı yap ve değer döndür işlemi yap
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //permission granted izin verildiyse
                    if (ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                        //Lokasyonu konumu güncelle
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, locationListener);
                    }
                }
                else {
                    //permission denied
                    //izin verilmediyse toast mesajı ver.
                    Toast.makeText(MapsActivity.this, "Permission needed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}