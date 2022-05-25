package com.example.shopapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Add extends AppCompatActivity {

    EditText companyName, contactName, mobileNumber;
    Button add, click, upload;
    FusedLocationProviderClient fusedLocationProviderClientU;
    ImageView imageView;
    TextView lat, longi, address;
    Uri uri;
    Shop shop;
    public int cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        companyName = (EditText) findViewById(R.id.company);
        contactName = (EditText) findViewById(R.id.contact);
        mobileNumber = (EditText) findViewById(R.id.mobile);
        imageView = (ImageView) findViewById(R.id.image);
        add = (Button) findViewById(R.id.add);
        upload = (Button) findViewById(R.id.upload);

        lat = (TextView) findViewById(R.id.latitudeAdd);
        longi = (TextView) findViewById(R.id.longitudeAdd);
        address = (TextView) findViewById(R.id.address);
        shop = new Shop();
        fusedLocationProviderClientU = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(Add.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            showLocation();
        } else {
            ActivityCompat.requestPermissions(Add.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            showLocation();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null){
                    uploadToDatabase(uri);
                }else{
                    Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        click = (Button) findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //askCameraPermission();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode==RESULT_OK && data!=null) {
            uri = data.getData();
            imageView.setImageURI(uri);
        }
    }

    private void uploadToDatabase(Uri uri) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getFileExtension(uri));
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        shop.setImage(uri.toString());
                        Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Could not Upload", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void add() {
        shop.setCompanyName(companyName.getText().toString());
        shop.setContactName(contactName.getText().toString());
        shop.setMobileNumber(mobileNumber.getText().toString());
        shop.setTimeStamp(System.currentTimeMillis());

        shop.setId(MainActivity.count);
        cnt = MainActivity.count;

        FirebaseDatabase.getInstance().getReference().child("Count").setValue(cnt);
        FirebaseDatabase.getInstance().getReference().child("Shop")
                .child(cnt + "").setValue(shop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        MainActivity.count += 1;
                        Toast.makeText(getApplicationContext(), "Inserted Data!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to Insert Data", Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void showLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClientU.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location=task.getResult();
                if(location!=null) {
                    Geocoder geocoder = new Geocoder(Add.this, Locale.getDefault());

                    List<Address> addresses  = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                        String addres = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String zip = addresses.get(0).getPostalCode();
                        String country = addresses.get(0).getCountryName();
                        address.setText(addres+", "+city+", "+state+", "+", "+country+" - "+zip);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    shop.setLatitude(location.getLatitude()+"");
                    shop.setLongitude(location.getLongitude()+"");
                    lat.setText("Latitude: " + shop.getLatitude());
                    longi.setText("Longitude: " + shop.getLongitude());
                }
                else {
                    Toast.makeText(Add.this,"Location is null",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}