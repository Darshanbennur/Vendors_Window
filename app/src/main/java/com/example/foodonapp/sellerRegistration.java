package com.example.foodonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class sellerRegistration extends AppCompatActivity {

    EditText sellerName, sellerShopName, sellerPhoneNumber, sellerDescription, sellerTiming, sellerPassword;
    Button register;
    TextView loginHere;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initializer();

        sharedPreferences = getSharedPreferences("sellers",MODE_PRIVATE);

        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vendorName = sellerName.getText().toString();
                String vendorShopName = sellerShopName.getText().toString();
                String vendorPhone = sellerPhoneNumber.getText().toString();
                String vendorDescription = sellerDescription.getText().toString();
                String vendorTiming = sellerTiming.getText().toString();
                String pass = sellerPassword.getText().toString();

                if (vendorName.isEmpty() || vendorShopName.isEmpty() || vendorPhone.isEmpty() || vendorDescription.isEmpty() || vendorTiming.isEmpty()){
                    Toast.makeText(sellerRegistration.this, "Enter Each Detail", Toast.LENGTH_SHORT).show();
                }
                else if (pass.length() < 8){
                    Toast.makeText(sellerRegistration.this, "Password should be of atleast 8 Characters", Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("sellers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(vendorPhone)){
                                Toast.makeText(sellerRegistration.this, "This Number is Already Registered!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                sharedPreferences.edit().putString("number",vendorPhone).apply();
                                databaseReference.child("sellers").child(vendorPhone).child("Name").setValue(vendorShopName);
                                databaseReference.child("sellers").child(vendorPhone).child("sellingItem").setValue(vendorShopName);
                                databaseReference.child("sellers").child(vendorPhone).child("Timings").setValue(vendorTiming);
                                databaseReference.child("sellers").child(vendorPhone).child("Password").setValue(pass);
                                Toast.makeText(sellerRegistration.this, "Vendor Registration Successful!!", Toast.LENGTH_SHORT).show();
                                finish();
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

    @Override
    public void onBackPressed() {
        finish();
    }

    public void returnLogin(View view){
        finish();
    }

    public void initializer(){
    sellerName = findViewById(R.id.sellerName);
    sellerShopName = findViewById(R.id.sellerShopName);
    sellerPhoneNumber = findViewById(R.id.sellerPhoneNumber);
    sellerDescription = findViewById(R.id.sellerDescription);
    sellerTiming = findViewById(R.id.sellerTimings);
    sellerPassword = findViewById(R.id.sellerPassword);
    register = findViewById(R.id.registerButton);
    loginHere = findViewById(R.id.loginHere);
    }
}