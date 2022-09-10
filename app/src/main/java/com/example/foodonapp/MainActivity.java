package com.example.foodonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    DatabaseReference sellers = FirebaseDatabase.getInstance().getReference("sellers");
    TextView sellerName, sellingItem, timings, sellerNumber, status;
    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initializer();

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            MarkerOptions options = new MarkerOptions().position(latLng)
                                    .title("I'm Here");

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            googleMap.addMarker(options);

                            sellers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotini) {
                                    MarkerOptions sellerShow;
                                    for(DataSnapshot snapshot1 : snapshotini.getChildren()){
                                        Double lati = 18.92214080631194, longi = 72.83397901708776;
                                        String Name = null;
                                        for(DataSnapshot snapshot2 : snapshot1.getChildren()){
                                            if(Objects.equals(snapshot2.getKey(), "lat"))
                                                lati=snapshot2.getValue(Double.class);
                                            if(Objects.equals(snapshot2.getKey(), "longi"))
                                                longi=snapshot2.getValue(Double.class);
                                        }
                                        phoneNumber = snapshot1.getKey();
                                        LatLng sellerlatLng = new LatLng(lati,longi);
                                        sellerShow = new MarkerOptions().position(sellerlatLng).title(snapshot1.getKey()).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_food_bank_24));
                                        googleMap.addMarker(sellerShow);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    phoneNumber = marker.getTitle();
                                    sellers.child(phoneNumber).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot snapshot4 : snapshot.getChildren()){
                                                if(Objects.equals(snapshot4.getKey(), "Name"))
                                                    sellerName.setText(snapshot4.getValue(String.class));
                                                if(Objects.equals(snapshot4.getKey(), "Status"))
                                                    status.setText(snapshot4.getValue(String.class));
                                                if(Objects.equals(snapshot4.getKey(), "Timings"))
                                                    timings.setText(snapshot4.getValue(String.class));
                                                if(Objects.equals(snapshot4.getKey(), "sellingItem"))
                                                    sellingItem.setText(snapshot4.getValue(String.class));
                                            }
                                            sellerNumber.setText(phoneNumber);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    return false;
                                }
                            });



                        }
                    });
                }
            }
        });
    }

    public void contactSeller(View view){
        TextView txtview = (TextView) view;
        Intent intent_04 = new Intent(Intent.ACTION_DIAL);
        intent_04.setData(Uri.parse("tel:"+txtview.getText().toString()));
        startActivity(intent_04);
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }


    public void initializer(){
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_maper);
        client = LocationServices.getFusedLocationProviderClient(this);
        sellerName = findViewById(R.id.name);
        sellingItem = findViewById(R.id.whatSell);
        timings = findViewById(R.id.timings);
        sellerNumber = findViewById(R.id.PhoneNumber);
        status = findViewById(R.id.Status);
    }
}