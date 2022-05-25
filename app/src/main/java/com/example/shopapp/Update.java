package com.example.shopapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Update extends AppCompatActivity {

    EditText companyName, contactName, mobileNumber;
    Button update, delete, click, upload, checkLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView imageView;
    TextView updateLatitude, updateLongitude, updateAddress;
    TextView view;
    Uri uri;
    Shop model;

    private static final int REQUEST_LOCATION = 1;
    public int cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        model = (Shop) (getIntent().getSerializableExtra("model"));

        view = (TextView) findViewById(R.id.View);
        view.setText("View " + model.getId());
        companyName = (EditText) findViewById(R.id.updateCompanyName);
        companyName.setText(model.getCompanyName());
        contactName = (EditText) findViewById(R.id.updateContactName);
        contactName.setText(model.getContactName());
        mobileNumber = (EditText) findViewById(R.id.updateMobileNumber);
        mobileNumber.setText(model.getMobileNumber());
        imageView = (ImageView) findViewById(R.id.Uimage);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        updateLatitude = (TextView) findViewById(R.id.latitude);
        updateLongitude = (TextView) findViewById(R.id.longitude);
        updateAddress = (TextView) findViewById(R.id.updateAddress);
        updateLatitude.setText(model.getLatitude());
        updateLongitude.setText(model.getLongitude());
        updateAddress.setText(model.getAddress());

        Glide.with(this)
                .load(model.getImage())
                .into(imageView);


        update = (Button) findViewById(R.id.update);
        delete = (Button) findViewById(R.id.delete);
        upload = (Button) findViewById(R.id.updateUpload);


        checkLocation = (Button) findViewById(R.id.updateLocation);
        checkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(Update.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                showLocation();
            } else {
                ActivityCompat.requestPermissions(Update.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                showLocation();
            }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {
                    uploadToDatabase(uri);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        click = (Button) findViewById(R.id.updateClick);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });
    }

    private void delete() {
        FirebaseDatabase.getInstance().getReference().child("Shop").child(model.getId()+"").removeValue();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void update() {
        model.setCompanyName(companyName.getText().toString());
        model.setContactName(contactName.getText().toString());
        model.setMobileNumber(mobileNumber.getText().toString());
        model.setTimeStamp(System.currentTimeMillis());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Shop");

        ref.child(model.getId()+"").setValue(model);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    private void uploadToDatabase(Uri uri) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getFileExtension(uri));
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        model.setImage(uri.toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode==RESULT_OK && data!=null) {
            uri = data.getData();
            imageView.setImageURI(uri);
        }
    }

    private void showLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location=task.getResult();
                if(location!=null) {
                    Geocoder geocoder = new Geocoder(Update.this, Locale.getDefault());

                    List<Address> addresses  = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                        String addres = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String zip = addresses.get(0).getPostalCode();
                        String country = addresses.get(0).getCountryName();
                        updateAddress.setText(addres+", "+city+", "+state+", "+", "+country+" - "+zip);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    model.setLatitude(location.getLatitude()+"");
                    model.setLongitude(location.getLongitude()+"");
                    updateLatitude.setText("Latitude: " + model.getLatitude());
                    updateLongitude.setText("Longitude: " + model.getLongitude());
                    updateAddress.setText("Address: " + updateAddress.getText().toString());
                }
                else {
                    Toast.makeText(Update.this,"Location is null",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}