package com.example.foodonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ConcurrentMap;

public class sellerLogin<EditView> extends AppCompatActivity {

    EditText sellerPhone, sellerPassword;
    Button login,userPass;
    TextView registerHere;
    SupportMapFragment supportMapFragment_01;
    FusedLocationProviderClient clientprovider;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Switch status;
    Location liveLocation;
    SharedPreferences sharedPreferences;
    String num = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initializer();


        sharedPreferences = getSharedPreferences("sellers",MODE_PRIVATE);
        supportMapFragment_01 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_maper);
        clientprovider = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),sellerRegistration.class);
                startActivity(intent);
            }
        });

        userPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num = sellerPhone.getText().toString();
                String pass = sellerPassword.getText().toString();

                if (num.isEmpty() || pass.isEmpty()){
                    Toast.makeText(sellerLogin.this, "Enter All Details", Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("sellers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(num)){
                                final String vendorPass = snapshot.child(num).child("Password").getValue(String.class);
                                if (pass.equals(vendorPass)){
                                    Toast.makeText(sellerLogin.this, "Logged in/ Queries Updated", Toast.LENGTH_SHORT).show();
                                    sellerPhone.setText("");
                                    sellerPassword.setText("");
                                    sharedPreferences.edit().putString("id",num).apply();
                                    if (status.isChecked()){
                                        databaseReference.child("sellers").child(num).child("Status").setValue("Open");
                                    }
                                    else{
                                        databaseReference.child("sellers").child(num).child("Status").setValue("Closed");
                                    }
                                    databaseReference.child("sellers").child(num).child("lat").setValue(liveLocation.getLatitude());
                                    databaseReference.child("sellers").child(num).child("longi").setValue(liveLocation.getLongitude());
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(sellerLogin.this, "Incorrect Password!!", Toast.LENGTH_SHORT).show();
                                    sellerPassword.setText("");
                                }
                            }
                            else{
                                Toast.makeText(sellerLogin.this, "Phone Number Not Registered!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            getlivelocation();
        else
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    public void getlivelocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Toast.makeText(this, "no permissions", Toast.LENGTH_SHORT).show();
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> tasker = clientprovider.getLastLocation();
        tasker.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    liveLocation=location;
                }
                else {
                    Toast.makeText(sellerLogin.this, "location not got", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initializer(){
        supportMapFragment_01 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_maper);
        sellerPhone = findViewById(R.id.sellerPhoneNumber);
        sellerPassword = findViewById(R.id.sellerPassword);
        login = findViewById(R.id.loginButton);
        registerHere = findViewById(R.id.registerHere);
        userPass = findViewById(R.id.userPass);
        status = findViewById(R.id.status);
    }

}